package cn.aicamera.frontend.network

import cn.aicamera.frontend.network.service.ChatService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://backend-url.com/") // TODO：修改后端url
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val chatService: ChatService = retrofit.create(ChatService::class.java)
}