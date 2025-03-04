package cn.aicamera.frontend.ui.copywriting

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.aicamera.frontend.R
import cn.aicamera.frontend.model.Message
import cn.aicamera.frontend.ui.theme.CameraAppTheme
import cn.aicamera.frontend.viewmodel.ChatViewModel

class CopywritingActivity : ComponentActivity() {
    private val selectedImageUris = mutableStateListOf<Uri>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imagePath : Uri? = intent.getParcelableExtra("image_path")

        if(imagePath==null){
            checkAndRequestPermission()
        }
        else selectedImageUris.add(imagePath)
        setContent {

                CameraAppTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val imageUris by remember { mutableStateOf(selectedImageUris)}
                        val viewModel : ChatViewModel = hiltViewModel()
                        viewModel.initMessageBox()
                        viewModel.addMessage(Message(text = stringResource(R.string.chat_welcome),isUser = false))
                        CommunicationScreen(imageUris)
                    }
            }
        }
    }

    /**
     * 检查权限并打开相册
     */
    private fun checkAndRequestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else { // Android 12 及以下
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(this, "需要存储权限才能访问相册", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        pickImagesLauncher.launch(arrayOf("image/*")) // 仅选择图片
    }
    private val pickImagesLauncher = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri>? ->
        uris?.let {
            selectedImageUris.clear()
            selectedImageUris.addAll(it.take(9)) // 清空后，选择最多9张照片
        }
    }
}