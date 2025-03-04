package cn.aicamera.frontend.ui.camera.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import cn.aicamera.frontend.MainActivity
import cn.aicamera.frontend.R
import cn.aicamera.frontend.ui.camera.PhotoPreviewScreen
import kotlinx.coroutines.delay
import java.io.File
import kotlin.math.max
import kotlin.math.min


@Composable
fun CameraScreen() {
    val context = LocalContext.current

    // 状态：是否有相机权限
    var hasCameraPermission by remember {
        mutableStateOf(checkCameraPermission(context))
    }

    // 权限请求启动器
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
        }
    )

    // 如果没有权限，显示请求权限的 UI
    if (!hasCameraPermission) {
        PermissionRequestUI {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    } else {
        // 如果有权限，显示相机 UI
        CameraContent()
    }
}

/**
 * 检查是否有相机权限
 */
private fun checkCameraPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * 请求权限的 UI
 */
@Composable
private fun PermissionRequestUI(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = buildBoldString(stringResource(R.string.camera_permission_required)),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRequestPermission,
                modifier = Modifier.height(100.dp)
            ) {
                Text(
                    text = stringResource(R.string.grant_permission),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }
    }
}

private fun buildBoldString(fullText: String): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        var isBold = false
        while (currentIndex < fullText.length) {
            // <b>标签会被自动删除，所以要使用**
            if (fullText.startsWith("**", currentIndex)) {
                isBold = !isBold
                currentIndex += 2
            } else {
                // 添加文字
                if (isBold) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(fullText[currentIndex].toString())
                    }
                } else {
                    append(fullText[currentIndex].toString())
                }
                currentIndex++
            }
        }
    }
}

/**
 * 相机界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraContent() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    var camera: Camera? = null // 记录绑定的相机变量

    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) } //默认选择后置相机

    // 相机缩放状态
    var scale by remember { mutableStateOf(1f) }
    val maxScale = 5f //最大缩放倍率

    val imageCapture = remember { ImageCapture.Builder().build() } // 拍照功能
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showPhotoPreview by remember { mutableStateOf(false) } // 记录是否完成一次拍照，跳转到保存页面

    // 聚焦框
    var focusPoint by remember { mutableStateOf<Offset?>(null) } // 焦点位置
    var showFocusIndicator by remember { mutableStateOf(false) } // 框控制
    val focusIndicatorAlpha by animateFloatAsState(
        targetValue = if (showFocusIndicator) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    )
    LaunchedEffect(showFocusIndicator) {
        if (showFocusIndicator) {
            delay(1000) // 1秒后消失
            showFocusIndicator = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 相机预览区域
            Box(
                modifier = Modifier
                    .height(screenHeight * 0.5f)
                    .fillMaxWidth()
                    .weight(1f)
                    .clipToBounds()
                    // 手势设置
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            // 监听双指缩放，进行画面缩放
                            scale = max(1f, min(scale * zoom, maxScale)) // 限制缩放范围
//                            scale = max(0f, min(scale + (zoom - 1) * 0.1f, 1f))
                            camera?.cameraControl?.setZoomRatio(scale)
//                            camera?.cameraControl?.setLinearZoom(scale)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            // 监听点击，进行聚焦
                            val factory = (context as? ComponentActivity)?.let { // 获取绑定了的相机焦点
                                (it.findViewById<PreviewView>(R.id.preview_view)).meteringPointFactory
                            }
                            factory?.let { pointFactory ->
                                // 创建聚焦操作
                                val action = FocusMeteringAction
                                    .Builder(
                                        pointFactory.createPoint(tapOffset.x, tapOffset.y)
                                    )
                                    .build()
                                camera?.cameraControl?.startFocusAndMetering(action)

                                // 显示聚焦框
                                focusPoint = tapOffset
                                showFocusIndicator = true
                            }
                        }
                    }
            ) {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            id = R.id.preview_view
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            transformOrigin = TransformOrigin(0f, 0f), // 缩放锚点
                            compositingStrategy = CompositingStrategy.Auto // 硬件加速
                        ),
                    update = { previewView ->
                        camera = bindCameraView(
                            context,previewView,lifecycleOwner,cameraSelector,imageCapture
                        )
                    }
                )
//                FocusCanvas(focusPoint, focusIndicatorAlpha) // 聚焦框
                ArrowAnimation(
                    arrowShow = focusPoint,
                    start = Offset(500f, 500f),
                    end = Offset(300f, 300f),
                    text = "跟随箭头移动手机",
                    duration = 1000, // 毫秒
                )
            }
            FunctionArea({
                scale = max(1f, min(scale + it, 5f))
                camera?.cameraControl?.setZoomRatio(scale)
            }, {
                if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                else
                    cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            }, {
                takePhoto(imageCapture, context) { bitmap ->
                    capturedBitmap = bitmap
                    showPhotoPreview = true
                }
            }, scale)
        }
    }


    if (showPhotoPreview && capturedBitmap != null) { // 下方窗口的渲染条件，如果条件不满足，会回到该页面
        PhotoPreviewScreen(
            bitmap = capturedBitmap!!,
            onDismiss = { showPhotoPreview = false }
        )
    }
}
// 提取出这个方法是为了调动cameraSelector的响应
// 在update时，可能由于addListener的原因，不糊响应cameraSelector的变化（仅单独提取出try catch无用）
private fun bindCameraView(
    context: Context,
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    imageCapture: ImageCapture
    ): Camera? {
    var camera : Camera? = null
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        // 设置相机预览，是图像的预览界面，而非UI的预览
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }
        try {
            // 绑定相机生命周期
            cameraProvider.unbindAll()
            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, ContextCompat.getMainExecutor(context))
    return camera
}

private fun takePhoto(
    originImageCapture: ImageCapture,
    context: Context,
    onPhotoCaptured: (Bitmap) -> Unit
) {
    val imageCapture = originImageCapture ?: return
    // 临时照片文件
    val photoFile = File.createTempFile("photo_", ".jpg", context.cacheDir)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    // 拍照
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // 将照片文件转换为Bitmap
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                onPhotoCaptured(bitmap) // 传递给回调函数
                photoFile.delete() // 删除临时文件
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraScreen", "Failed to take photo", exception)
            }
        }
    )
}


@androidx.compose.ui.tooling.preview.Preview
@Composable
fun TestFuntionArea() {

}