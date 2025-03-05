package cn.aicamera.frontend.network.service

import cn.aicamera.frontend.model.GeneralResponse
import cn.aicamera.frontend.model.LoginRequest
import cn.aicamera.frontend.model.RegisterRequest
import cn.aicamera.frontend.model.SuccessResponse
import cn.aicamera.frontend.model.UserProfile
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserService {
    // 用户注册
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<GeneralResponse<String>>

    // 用户登录
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<GeneralResponse<String>>

    // 退出登录
    @POST("auth/logout")
    suspend fun logout(): Response<SuccessResponse>

    // 更新用户信息，返回新用户信息
    @PUT("user/update")
    suspend fun updateUserProfile(@Body request: UserProfile): Response<GeneralResponse<UserProfile>>

    // 上传头像，返回头像链接
    @Multipart
    @POST("user/upload-avatar")
    suspend fun uploadAvatar(@Part avatar: MultipartBody.Part): Response<GeneralResponse<String>>
}