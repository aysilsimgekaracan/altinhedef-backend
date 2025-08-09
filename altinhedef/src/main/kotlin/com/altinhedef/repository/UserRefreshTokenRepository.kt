package com.altinhedef.repository

import com.altinhedef.entity.User
import com.altinhedef.entity.UserRefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRefreshTokenRepository: JpaRepository<UserRefreshToken, Long> {
    fun findByToken(token: String): Optional<UserRefreshToken>

    fun countByUser(user: User): Long

    fun findFirstByUserOrderByExpiryDateAsc(user: User): Optional<UserRefreshToken>
}