package cn.aicamera.frontend.model

// 用户信息
data class UserProfile(
    val id: String,
    val nickname: String,
    val gender: String,
    val age: Int?,
    val email: String,
    val preference: String,
    val avatarUrl: String?
)
// 注册请求
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

// 登录请求
data class LoginRequest(
    val email: String,
    val password: String
)
