package cn.aicamera.frontend.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.aicamera.frontend.model.UserProfile
import cn.aicamera.frontend.network.TokenManager
import cn.aicamera.frontend.network.service.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val userService: UserService
) : ViewModel() {
    private val _user = MutableStateFlow(
        UserProfile(
            "001",
            "张三",
            "男",
            25,
            "user@example.com",
            "编程",
            "https://example.com/avatar.jpg"
        )
    )
    val user: StateFlow<UserProfile> = _user

    fun updateProfile(
        nickname: String,
        gender: String,
        age: Int,
        email: String,
        preference: String,
        onSuccess: () -> Unit, onFailed: (String) -> Unit
    ) {
        viewModelScope.launch {
            val response = userService.updateUserProfile(
                UserProfile(
                    _user.value.id,
                    nickname,
                    gender,
                    age,
                    email,
                    preference,
                    avatarUrl = null
                )
            )
            if (response.isSuccessful && response.body()!!.success) {
                _user.value = _user.value.copy(
                    nickname = nickname,
                    gender = gender,
                    age = age,
                    email = email,
                    preference = preference
                )
                onSuccess()
            } else {
                Log.e("Update Profile", "Update profile failed:${response.body()!!.message}")
                onFailed(response.body()?.message ?: "信息上传失败，请联系管理员")
            }
        }
    }


    fun uploadAvatar(uri: Uri, onSuccess: () -> Unit, onFailed: (String) -> Unit) {
        viewModelScope.launch {
            val file = File(uri.path!!) // 从 URI 获取文件路径
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("avatar", file.name, requestBody)

            val response = userService.uploadAvatar(part)
            if (response.isSuccessful && response.body()!!.success) {
                _user.value =
                    _user.value.copy(avatarUrl = response.body()?.response ?: _user.value.avatarUrl)
                onSuccess()
            } else {
                Log.e("Update Avatar", "Upload avatar failed:${response.body()!!.message}")
                onFailed(response.body()?.message ?: "头像上传失败，请联系管理员")
            }
        }
    }

    fun logout(onSuccess: () -> Unit, onFailed: (String) -> Unit) {
        viewModelScope.launch {
            val response = userService.logout()
            if (response.isSuccessful && response.body()!!.success) {
                tokenManager.clearToken()
                onSuccess()
            } else {
                Log.e("Log Out", "Log out failed:${response.body()!!.message}")
                onFailed(response.body()?.message ?: "退出登录失败，请联系管理员")
            }
        }
    }
}