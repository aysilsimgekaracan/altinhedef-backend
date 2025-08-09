package com.altinhedef.dto.response.user

import java.time.LocalDateTime

data class UserResponse (
    val id: Long,
    val name: String,
    val surname: String,
    val email: String,
    val role: String,
    val createdAt: LocalDateTime?
)
