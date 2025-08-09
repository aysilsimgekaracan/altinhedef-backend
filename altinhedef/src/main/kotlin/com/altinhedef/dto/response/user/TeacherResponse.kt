package com.altinhedef.dto.response.user

import java.time.LocalDateTime

data class TeacherResponse (
    val id: Long,
    val name: String,
    val surname: String,
    val email: String,
    val role: String,
    val createdAt: LocalDateTime?,
    val bio: String?
)