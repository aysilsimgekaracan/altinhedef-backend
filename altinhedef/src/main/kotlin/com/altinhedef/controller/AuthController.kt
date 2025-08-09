package com.altinhedef.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.responses.ApiResponse as OpenApiResponse
import jakarta.validation.Valid
import com.altinhedef.dto.ApiResponse
import com.altinhedef.dto.auth.LoginRequest
import com.altinhedef.dto.auth.RefreshTokenRequest
import com.altinhedef.dto.auth.RegisterRequest
import com.altinhedef.dto.response.user.LoginResponse
import com.altinhedef.dto.response.user.UserResponse
import com.altinhedef.entity.User
import com.altinhedef.service.AuthService
import com.altinhedef.service.JwtService
import com.altinhedef.service.LoginAttemptService
import com.altinhedef.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User Register and Login Operations")
class AuthController(
    private val userService: UserService,
    private val authService: AuthService,
    private val loginAttemptService: LoginAttemptService
) {
    @Operation(
        summary = "Register New Student",
        description = "Adds a user to the system with 'STUDENT' role",
        responses = [
            OpenApiResponse(responseCode = "201", description = "User register is successful."),
            OpenApiResponse(responseCode = "409", description = "This email address is already registered.", content = [Content(schema = Schema(implementation = ApiResponse::class))]),
            OpenApiResponse(responseCode = "400", description = "Bad request (password policy etc.)", content = [Content(schema = Schema(implementation = ApiResponse::class))])
        ]
    )
    @PostMapping("register")
    fun registerUser(@Valid @RequestBody request: RegisterRequest): ResponseEntity<ApiResponse<UserResponse>> {
        return try {
            val newUser = userService.registerUser(request)
            val response = ApiResponse(
                success = true,
                message = "Kullanıcı başarıyla oluşturuldu.",
                data = newUser.toUserResponse()
            )
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: IllegalStateException) {
            val response = ApiResponse<UserResponse>(
                success = false,
                message = e.message ?: "Bu e-posta zaten kullanımda."
            )
            ResponseEntity.status(HttpStatus.CONFLICT).body(response)
        } catch (e: MethodArgumentNotValidException) {
            val response = ApiResponse<UserResponse>(
                success = false,
                message = e.message
            )
            ResponseEntity.status(HttpStatus.CONFLICT).body(response)
        } catch (e: Exception) {
            val response = ApiResponse<UserResponse>(
                success = false,
                message = "Sunucu tarafında beklenmedik bir hata oluştu."
            )
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        }
    }

    @Operation(
        summary = "User Login",
        description = "It validates user's email and password. If it valides, it returns access and refresh tokens",
        responses = [
            OpenApiResponse(responseCode = "200", description = "Login successful.", content = [Content(schema = Schema(implementation = LoginResponse::class))]),
            OpenApiResponse(responseCode = "401", description = "Email or password is wrong."),
            OpenApiResponse(responseCode = "423", description = "Account is locked due to too many unsuccessful attempts")
        ]
    )
    @PostMapping("login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        try {
            val loginData = authService.login(request)
            val response = ApiResponse(true, "Giriş Başarılı", loginData)
            return ResponseEntity.ok(response)
        } catch (e: BadCredentialsException) {
            loginAttemptService.loginFailed(request.email)
            val errorResponse = ApiResponse<LoginResponse>(
                success = false,
                message = "E-posta veya şifre hatalı"
            )
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
        } catch (e: IllegalStateException) {
            val errorResponse = ApiResponse<LoginResponse>(
                success = false,
                message = e.message ?: "Bir hata oluştu"
            )
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse)
        }
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<LoginResponse>> {
        try {
            val newTokens = authService.refreshToken(request.refreshToken)
            val response = ApiResponse(true, "Token başarıyla yenilendi", newTokens)
            return ResponseEntity.ok(response)
        } catch (e: IllegalArgumentException) {
            val errorResponse = ApiResponse<LoginResponse>(false, e.message ?: "Geçersiz refresh token")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
        } catch (e: Exception) {
            val response = ApiResponse<LoginResponse>(
                success = false,
                message = "Sunucu tarafında beklenmedik bir hata oluştu."
            )
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        }
    }

    @Operation(
        summary = "Refresh Access Token",
        description = "Request a new access token with using a refresh token.",
        responses = [
            OpenApiResponse(responseCode = "200", description = "Token has been refreshed", content = [Content(schema = Schema(implementation = LoginResponse::class))]),
            OpenApiResponse(responseCode = "401", description = "Invalid refresh token")
        ]
    )
    private fun User.toUserResponse(): UserResponse {
        return UserResponse(
            id = this.id!!,
            name = this.name,
            surname = this.surname,
            email = this.email,
            role = this.role.name,
            createdAt = this.createdAt
        )
    }
}