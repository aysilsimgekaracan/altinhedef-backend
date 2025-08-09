package net.altinhedef.altinhedef.dto.auth

data class CreateTeacherRequest(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val bio: String?
)