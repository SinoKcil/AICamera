package cn.aicamera.frontend.ui.copywriting

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.getstream.chat.android.compose.ui.messages.MessagesScreen
import io.getstream.chat.android.compose.ui.messages.attachments.factory.AttachmentsPickerTabFactories
import io.getstream.chat.android.compose.ui.theme.ChatComponentFactory
import io.getstream.chat.android.compose.ui.theme.ChatTheme
import io.getstream.chat.android.compose.viewmodel.messages.MessageListViewModel
import io.getstream.chat.android.compose.viewmodel.messages.MessagesViewModelFactory
import io.getstream.chat.android.models.Channel
import io.getstream.chat.android.models.ConnectionState
import io.getstream.chat.android.models.User
import io.getstream.chat.android.ui.common.state.messages.MessageMode
import io.getstream.chat.android.ui.common.state.messages.composer.MessageComposerState

@Composable
fun CommunicationScreen(imagePath: Uri?){
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    var bitmap : Bitmap? = null
    try {
        if(imagePath == null) throw NullPointerException("Image path is null")
        val inputStream = context.contentResolver.openInputStream(imagePath)
        bitmap = BitmapFactory.decodeStream(inputStream)

    } catch (e: Exception) {
        e.printStackTrace()
        Text("图片加载失败或被删除")
    }
    Column {
        var boxHeight by remember { mutableStateOf(Math.floor(configuration.screenHeightDp*0.2).toInt()) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(boxHeight.dp)
                .drawBehind { // 绘制一条边框底部的线
                    val strokeWidth = 1f * density // 线的粗细，1为1dp
                    val y = size.height // 高度，size.height为box的高度
                    drawLine(
                        Color.LightGray,
                        Offset(0f, y),
                        Offset(size.width, y),
                        strokeWidth
                    )
                },
            contentAlignment = Alignment.Center,
        ){
            if(bitmap != null){
                val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
                val targetWidth = (boxHeight * aspectRatio).toInt()
                Image(
                    bitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, boxHeight, false).asImageBitmap(),
                    contentDescription = "待处理图片",
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(aspectRatio),
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f)
        ){
            ChatScreen(context)
        }
    }
}
@Composable
fun ChatScreen(context: Context){
    val channelId = "messaging:general"
    val viewModelFactory = MessagesViewModelFactory(context = context,channelId = channelId)
//    val messagesViewModel = viewModelFactory.create<MessageListViewModel>()
    val viewModel = viewModelFactory.create(MessageListViewModel::class.java)
    ChatTheme(
        componentFactory = CustomChatComponentFactory()
    ) {
        MessagesScreen(
            viewModelFactory = viewModelFactory
        )
    }
}

@Composable
fun DeprecatedFunctions(){
    // 自定义头部，无状态显示
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    color =
                    if (isSystemInDarkTheme()) Color.Black
                    else Color.White
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "自动文案生成",
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        // 消息展示
//        MessageList(
//            modifier = Modifier.weight(1f),
//            viewModel = viewModel,
//                onMessageLongPress = { message -> /* 处理长按 */ }
//        )
        // 自定义输入框

    }
}

class CustomChatComponentFactory : ChatComponentFactory {
    val defaultTabFactories = AttachmentsPickerTabFactories.defaultFactories(
        takeImageEnabled = false, // Disable camera
        recordVideoEnabled = false // Disable video recording
    )
    @Composable
    override fun RowScope.MessageComposerIntegrations(
        state: MessageComposerState,
        onAttachmentsClick: () -> Unit,
        onCommandsClick: () -> Unit,
    ) {
        // Only keep the attachments button
        IconButton(
            modifier = Modifier
                .size(48.dp)
                .padding(12.dp),
            onClick = onAttachmentsClick
        ){
            Icon( // 附件图标颜色
                imageVector = Icons.Filled.Attachment,
                contentDescription = "Send",
                tint = if(isSystemInDarkTheme()) Color.White else LocalContentColor.current
            )
            // TODO：修改附件选项中的按钮，只保留发送图片
        }
    }

    @Composable
    override fun RowScope.MessageListHeaderCenterContent(
        modifier: Modifier,
        channel: Channel,
        currentUser: User?,
        typingUsers: List<User>,
        messageMode: MessageMode,
        onHeaderTitleClick: (Channel) -> Unit,
        connectionState: ConnectionState,
    ) {
        // Your implementation for the message list header center content
        val title = when (messageMode) {
            MessageMode.Normal -> ChatTheme.channelNameFormatter.formatChannelName(channel, currentUser)
            is MessageMode.MessageThread -> "" // 原本是资源型字符串，找不到，遂放弃
        }

        Column(
            modifier = modifier
                .height(IntrinsicSize.Max)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onHeaderTitleClick(channel) },
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                modifier = Modifier.testTag("Stream_ChannelName"),
                text = title,
                style = ChatTheme.typography.title3Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = ChatTheme.colors.textHighEmphasis,
            )
        }
    }


}
@Preview(showBackground = true)
@Composable
fun Test(){
}