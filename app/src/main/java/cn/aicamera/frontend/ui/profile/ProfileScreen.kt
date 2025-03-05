package cn.aicamera.frontend.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.aicamera.frontend.common.SelectPicture
import cn.aicamera.frontend.viewmodel.UserViewModel
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun ProfileScreen(viewModel: UserViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val selectedImageUris = mutableStateListOf<Uri>()
    val galleryPermissionState =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            rememberPermissionState(Manifest.permission.READ_MEDIA_IMAGES)
        } else { // Android 12 及以下
            rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    val user by viewModel.user.collectAsState()
    val showEditDialog = remember { mutableStateOf(false) }
    val showAvatarDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 头像
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.Gray)
                .clickable { showAvatarDialog.value = true }
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize().clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 个人信息
        Text("昵称: ${user.nickname}", fontSize = 18.sp)
        Text("性别: ${user.gender}", fontSize = 18.sp)
        Text("年龄: ${user.age}", fontSize = 18.sp)
        Text("邮箱: ${user.email}", fontSize = 18.sp)
        Text("偏好: ${user.preference}", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(30.dp))

        // 修改信息按钮
        Button(onClick = { showEditDialog.value = true }) {
            Text("修改个人信息")
        }

        Spacer(modifier = Modifier.height(16.dp))

//        // 退出登录按钮
//        Button(onClick = { viewModel.logout() }, colors = ButtonDefaults.buttonColors(Color.Red)) {
//            Text("退出登录", color = Color.White)
//        }
    }

    // 弹出修改个人信息卡片
    if (showEditDialog.value) {
        EditProfileDialog(context, viewModel, onDismiss = { showEditDialog.value = false })
    }

    // 弹出修改头像对话框
    if (showAvatarDialog.value) {
        tryOpenGallery(context,galleryPermissionState) {
            selectedImageUris.clear()
            selectedImageUris.add(it)
        }
        ChangeAvatarDialog(
            context,
            viewModel,
            selectedImageUris,
            onDismiss = { showAvatarDialog.value = false })
    }
}

@Composable
fun EditProfileDialog(context: Context, viewModel: UserViewModel, onDismiss: () -> Unit) {
    val nickname = remember { mutableStateOf(viewModel.user.value.nickname) }
    val gender = remember { mutableStateOf(viewModel.user.value.gender) }
    val age = remember { mutableStateOf(viewModel.user.value.age.toString()) }
    val email = remember { mutableStateOf(viewModel.user.value.email) }
    val preference = remember { mutableStateOf(viewModel.user.value.preference) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("修改个人信息") },
        text = {
            Column {
                OutlinedTextField(
                    value = nickname.value,
                    onValueChange = { nickname.value = it },
                    label = { Text("昵称") })
                OutlinedTextField(
                    value = gender.value,
                    onValueChange = { gender.value = it },
                    label = { Text("性别") })
                OutlinedTextField(
                    value = age.value,
                    onValueChange = { age.value = it },
                    label = { Text("年龄") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("邮箱") })
                OutlinedTextField(
                    value = preference.value,
                    onValueChange = { preference.value = it },
                    label = { Text("偏好") })
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.updateProfile(nickname.value,
                    gender.value,
                    age.value.toIntOrNull() ?: 0,
                    email.value,
                    preference.value,
                    onSuccess = {
                        onDismiss()
                    },
                    onFailed = { message ->
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    })
            }) {
                Text("保存")
            }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("取消") } }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChangeAvatarDialog(
    context: Context,
    viewModel: UserViewModel,
    selectedImageUris: List<Uri>,
    onDismiss: () -> Unit
) {
    if(selectedImageUris.size!=1){
        Toast.makeText(context, "请选择一张图片", Toast.LENGTH_LONG).show()
        return
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("更换头像") },
        text = {
            AsyncImage(
            model = selectedImageUris[0],
            contentDescription = "预览图",
            modifier = Modifier
                .size(320.dp) // 缩略图大小
                .clip(CircleShape)
                .border(2.dp, Color.Gray, RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        ) },
        confirmButton = {
            Button(onClick = {
                viewModel.uploadAvatar(selectedImageUris[0],
                    onSuccess = { onDismiss() },
                    onFailed = { message ->
                        Toast.makeText(context, "图片上传失败:$message", Toast.LENGTH_LONG)
                            .show()
                    })
            }) {
                Text("确定更改")
            }
        },
        dismissButton = { Button(onClick = onDismiss) { Text("取消更改") } }
    )
}

@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun tryOpenGallery(
    context: Context,
    galleryPermissionState: PermissionState,
    onSelectImage: (Uri) -> Unit
) {
    when {
        galleryPermissionState.status.isGranted -> {
            var openGalleryLauncher: ManagedActivityResultLauncher<Unit?, Uri?>? = null
            openGalleryLauncher = rememberLauncherForActivityResult(contract = SelectPicture()) {uri->
                if(uri!=null) onSelectImage(uri)
                else{
                    Toast.makeText(context,"请至少选择一张图片",Toast.LENGTH_LONG).show()
                }
            }
        }
        else ->{
            galleryPermissionState.launchPermissionRequest()
        }
    }
}



