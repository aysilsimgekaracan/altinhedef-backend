package net.altinhedef.altinhedef.dto.auth

data class RegisterRequest(
    val name: String,
    val surname: String,
    val email: String,
    val password: String
)
