package net.altinhedef.altinhedef.service

import net.altinhedef.altinhedef.dto.auth.LoginRequest
import net.altinhedef.altinhedef.dto.response.user.LoginResponse
import net.altinhedef.altinhedef.entity.UserRefreshToken
import net.altinhedef.altinhedef.repository.UserRefreshTokenRepository
import net.altinhedef.altinhedef.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val refreshTokenRepository: UserRefreshTokenRepository
) {
    @Value("\${jwt.refresh-token.expiration-in-ms}")
    private val refreshTokenDurationMs: Long = 604800000 // 7 days

    fun login(request: LoginRequest): LoginResponse {

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.password
            )
        )

        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalStateException("Kullanıcı bulunamadı")

        val accessToken = jwtService.generateToken(user)

        val refreshToken = UserRefreshToken(
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(refreshTokenDurationMs),
            user = user
        )

        val savedRefreshToken = refreshTokenRepository.save(refreshToken)

        return LoginResponse(accessToken = accessToken, refreshToken = savedRefreshToken.token)
    }

    fun refreshToken(token: String): LoginResponse {
        val user = userRepository.findByRefreshTokens_Token(token)
            .orElseThrow { IllegalArgumentException("Geçersiz Refresh Token") }

        val refreshTokenEntity = user.refreshTokens.find { it.token == token }
            ?: throw IllegalStateException("Token, kullanıcıyla eşleşmedi.")

        if (refreshTokenEntity.expiryDate.isBefore(Instant.now())) {
            user.refreshTokens.remove(refreshTokenEntity)
            refreshTokenRepository.delete(refreshTokenEntity)
            throw IllegalArgumentException("Refresh token süresi dolmuş")
        }

        val newAccessToken = jwtService.generateToken(user)
        val newRefreshToken = UserRefreshToken(
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(refreshTokenDurationMs),
            user = user
        )

        val savedRefreshToken = refreshTokenRepository.save(newRefreshToken)

        return LoginResponse(accessToken = newAccessToken, refreshToken = savedRefreshToken.token)
    }

}