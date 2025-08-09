package net.altinhedef.altinhedef.service

import net.altinhedef.altinhedef.dto.auth.LoginRequest
import net.altinhedef.altinhedef.dto.response.user.LoginResponse
import net.altinhedef.altinhedef.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {
    private val REFRESH_EXPIRATION: Long = 604800 // 7 days
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
        val refreshToken = UUID.randomUUID().toString()

        user.refreshToken = refreshToken
        user.refreshTokenExpiry = Instant.now().plusSeconds(REFRESH_EXPIRATION)
        userRepository.save(user)

        return LoginResponse(accessToken = accessToken, refreshToken = refreshToken)
    }

    fun refreshToken(refreshToken: String): LoginResponse {
        val user = userRepository.findByRefreshToken(refreshToken)
            ?: throw IllegalArgumentException("Geçersiz Refresh Token")

        if (user.refreshTokenExpiry?.isBefore(Instant.now()) == true) {
            throw IllegalArgumentException("Refresh Token'ın süresi dolmuş.")
        }

        val newAccessToken = jwtService.generateToken(user)
        val newRefreshToken = UUID.randomUUID().toString()

        user.refreshToken = newRefreshToken

        userRepository.save(user)

        return LoginResponse(accessToken = newAccessToken, refreshToken = newRefreshToken)
    }

}