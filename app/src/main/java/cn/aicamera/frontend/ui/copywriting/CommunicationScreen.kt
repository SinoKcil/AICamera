package cn.aicamera.frontend.ui.copywriting

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.aicamera.frontend.R
import cn.aicamera.frontend.model.Message
import cn.aicamera.frontend.utils.FileUtils
import cn.aicamera.frontend.viewmodel.ChatViewModel
import coil.compose.AsyncImage
import kotlin.math.floor

@Composable
fun CommunicationScreen(
    imageUris: MutableList<Uri>, // 传入的图片 URI 列表
    viewModel: ChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val messages by viewModel.messages.collectAsState()
    val scrollState = rememberLazyListState()

    val defaultText = stringResource(R.string.chat_default)
    var inputText by remember { mutableStateOf(defaultText) }

    // 上传照片
    val iterator = imageUris.iterator()
    var i = 0;var count = 0
    while (iterator.hasNext()) {
        val imageUri = iterator.next()
        val file = FileUtils.uriToFile(uri = imageUri, context = context)
        if (file == null) Toast.makeText(context, "第${i}张照片打开失败", Toast.LENGTH_LONG).show()
        else {
            viewModel.uploadImageToServer(
                file, { count++ },
                {
                    Toast.makeText(context, "第${i}张照片上传失败", Toast.LENGTH_LONG).show()
                    iterator.remove()
                }
            )
        }
        i++
    }
    Column(Modifier.fillMaxSize()) {
        ImageGallery(imageUris,count == imageUris.size) // 顶部图片展示

        LazyColumn(
            state = scrollState,
            reverseLayout = true, // 最新消息在底部
            modifier = Modifier.weight(1f)
        ) {
            items(messages.reversed()) { message ->
                ChatBubble(message)
            }
        }

        ChatInputField(
            inputText = inputText,
            onTextChange = { inputText = it },
            onSendMessage = {
                if (inputText.isNotBlank()) {
                    viewModel.sendMessage(inputText.trim())
                    inputText = ""
                }
            }
        )
    }
}

/**
 * 聊天气泡
 */
@Composable
fun ChatBubble(message: Message) {
    val arrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    val backgroundColor = if (message.isUser) MaterialTheme.colorScheme.surfaceVariant else Color.LightGray

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val boxWidth = floor(screenWidth*0.7f.toDouble())
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ){
        // AI头像
        if(!message.isUser){
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(top = 8.dp, start = 8.dp)
                    .background(Color.Black, shape = RoundedCornerShape(48.dp))
            )
        }
        Box(
            modifier = Modifier
                .widthIn(max = boxWidth.dp)
                .heightIn(min = 56.dp)
                .padding(8.dp)
                .background(backgroundColor, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = message.text,
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 5.dp)
            )
        }
        // 用户头像
        if(message.isUser){
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(top = 8.dp, end = 8.dp)
                    .background(Color.Black, shape = RoundedCornerShape(48.dp))
            )
        }
    }
}

/**
 * 展示图片
 */
@Composable
fun ImageGallery(imageUris: List<Uri>,finishUpload: Boolean) {
    if (imageUris.isNotEmpty()&&finishUpload) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp)
        ) {
            Text(
                text = "图片预览",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(imageUris.take(9)) { uri -> // 最多显示 9 张图片
                    ImageThumbnail(uri)
                }
            }
        }
    }
    else{
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "图片上传中...",
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun ImageThumbnail(uri: Uri) {
    AsyncImage(
        model = uri,
        contentDescription = "预览图",
        modifier = Modifier
            .size(80.dp) // 缩略图大小
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

/**
 * 输入框部分
 */
@Composable
fun ChatInputField(
    inputText: String,
    onTextChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = inputText,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("输入消息...") },
        )
        IconButton(
            modifier = Modifier
                .size(48.dp)
                .padding(12.dp),
            onClick = onSendMessage
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ChatBubble() {
    val arrangement = if (false) Arrangement.End else Arrangement.End
    val backgroundColor = if (false) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFEAE1D9)

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val boxWidth = floor(screenWidth*0.7f.toDouble())
    Row(
        modifier = Modifier.padding(top = 100.dp).fillMaxWidth(),
        horizontalArrangement = arrangement
    ){
        Box(
            modifier = Modifier
                .size(48.dp)
                .padding(top = 8.dp, start = 8.dp)
                .background(Color.Black, shape = RoundedCornerShape(48.dp))
        )
        Box(
            modifier = Modifier
                .width(boxWidth.dp)
                .heightIn(min = 56.dp)
                .padding(8.dp)
                .background(backgroundColor, shape = RoundedCornerShape(8.dp))
        ) {
            Text(
                text = stringResource(R.string.chat_welcome),
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 4.dp, end = 2.dp, top = 2.dp, bottom = 2.dp)
            )
        }
    }
}


