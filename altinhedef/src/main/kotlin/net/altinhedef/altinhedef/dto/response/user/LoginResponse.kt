package net.altinhedef.altinhedef.dto.response.user

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)
