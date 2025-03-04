package cn.aicamera.frontend.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.aicamera.frontend.model.Message
import cn.aicamera.frontend.model.MessageRequest
import cn.aicamera.frontend.network.RetrofitClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    init {
        startSSEListener()
    }

    /**
     * 发送文字消息到后端
     */
    fun sendMessage(text: String) {
        viewModelScope.launch {
            try {
//                RetrofitClient.chatService.sendMessage(MessageRequest(text))
                _messages.value += Message(text = text, isUser = true)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "发送消息失败: ${e.message}")
            }
        }
    }

    /**
     * 发送图片到后端
     */
    fun uploadImageToServer(imageFile: File, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        viewModelScope.launch {
            try {
                delay(1000)
                onSuccess("this is test")

//                val response = RetrofitClient.chatService.uploadImage(imagePart)
//                onSuccess(response.url) // 返回图片的URL
            } catch (e: Exception) {
                onFailure(e.message ?: "上传失败")
            }
        }
    }

    fun initMessageBox(){
        _messages.value = emptyList()
    }
    fun addMessage(message: Message){
        _messages.value += message
    }
    private fun startSSEListener() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.chatService.listenMessages().execute()
                response.body()?.let { body ->
                    body.byteStream().bufferedReader().useLines { lines ->
                        lines.forEach { line ->
                            if (line.isNotBlank()) {
                                withContext(Dispatchers.Main) {
                                    _messages.value += Message(text = line, isUser = false)
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "SSE连接失败: ${e.message}")
            }
        }
    }
}