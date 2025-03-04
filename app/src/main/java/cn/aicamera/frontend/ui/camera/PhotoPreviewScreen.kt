package cn.aicamera.frontend.ui.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import cn.aicamera.frontend.R
import cn.aicamera.frontend.ui.component.ShareSheet
import cn.aicamera.frontend.ui.copywriting.CopywritingActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPreviewScreen(bitmap: Bitmap, onDismiss: () -> Unit) {
    val context = LocalContext.current

    // 是否有存储权限
    var hasStoragePermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    // 权限请求启动器
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasStoragePermission = isGranted
            if (isGranted) {
                savePhotoToGallery(bitmap, context)
            }
        }
    )

    var showShareSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                title = { Text(text = stringResource(R.string.camera_preview_title)) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                // 分享按钮
                actions = {
                    IconButton(
                        onClick = { showShareSheet = true }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share"
                        )
                    }
                }
            )
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 展示照片
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f) // 70% 高度
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ){
                if (bitmap != null) {
                    Image(
                    // 直接放照片会尺寸过大而崩溃
                    bitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth()*0.7).toInt(), (bitmap.getHeight()*0.6).toInt(), false).asImageBitmap(),
                    contentDescription = "Captured Photo",
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat())
                )
                }else{
                    Text("图片加载失败或为空")
                }
            }

            // 保存和其他功能区
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .background(MaterialTheme.colorScheme.primaryContainer),
            ){
                Column {
                    Text(
                        text= stringResource(R.string.camera_preview_notice),
                    )
                    Row (
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        // 保存按钮
                        Column(
                            modifier = Modifier.weight(0.5f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(
                                onClick = {},
                                colors = IconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            color = MaterialTheme.colorScheme.onTertiary,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            if (hasStoragePermission) {
                                                val uri = savePhotoToGallery(bitmap, context)
                                                if (uri != null) {
                                                    onDismiss() // 关闭预览窗口
                                                }
                                            } else {
                                                // 请求存储权限
                                                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ){

                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = "保存到手机",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            Text("保存")
                        }
                        // 文案生成页面按钮
                        Column (
                            modifier = Modifier.weight(0.5f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(
                                onClick = {},
                                colors = IconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.primary,
                                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            color = MaterialTheme.colorScheme.onTertiary,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            if (hasStoragePermission) {
                                                val uri = savePhotoToGallery(bitmap, context)
                                                if (uri != null) {
                                                    val intent = Intent(
                                                        context,
                                                        CopywritingActivity::class.java
                                                    )
                                                    intent.putExtra("image_path", uri as Uri)
                                                    context.startActivity(intent)
                                                }
                                            } else {
                                                // 请求存储权限
                                                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {

                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "自动生成文案",
                                        modifier = Modifier.size(56.dp)
                                    )
                                }
                            }
                            Text(
                                text = "保存并自动生成文案",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

        }
        ShareSheet(showShareSheet,{
            showShareSheet = false
        }, bitmap =  bitmap)
    }
}

/**
 * 将照片保存到相册
 */
private fun savePhotoToGallery(bitmap: Bitmap, context: Context) : Uri? {
    val savedUri = bitmap.saveToGallery(context)
    if (savedUri != null) {
        Toast.makeText(context, R.string.save_image_success,Toast.LENGTH_SHORT).show()
        Log.d("PhotoPreviewScreen", "Photo saved to gallery: $savedUri")
        return savedUri
    } else {
        Toast.makeText(context,R.string.save_image_failed,Toast.LENGTH_SHORT).show()
        Log.e("PhotoPreviewScreen", "Failed to save photo to gallery")
        return null
    }
}

/**
 * 将 Bitmap 保存到相册
 */
private fun Bitmap.saveToGallery(context: Context): android.net.Uri? {
    val contentValues = android.content.ContentValues().apply {
        put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
        put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraApp")
        }
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    return uri?.also {
        resolver.openOutputStream(it)?.use { outputStream ->
            this.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun Test(){
}