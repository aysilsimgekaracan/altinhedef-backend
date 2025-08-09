package com.altinhedef.repository

import com.altinhedef.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    fun existsByRole_Name(roleName: String): Boolean

    fun findByRefreshTokens_Token(token: String): Optional<User>
}