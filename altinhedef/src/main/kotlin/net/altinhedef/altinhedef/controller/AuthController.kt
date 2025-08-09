package net.altinhedef.altinhedef.controller

import net.altinhedef.altinhedef.dto.ApiResponse
import net.altinhedef.altinhedef.dto.auth.RegisterRequest
import net.altinhedef.altinhedef.dto.response.user.UserResponse
import net.altinhedef.altinhedef.entity.User
import net.altinhedef.altinhedef.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/api/auth")
class AuthController(private val userService: UserService) {
    @PostMapping("register")
    fun registerUser(@RequestBody request: RegisterRequest): ResponseEntity<ApiResponse<UserResponse>> {
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
        } catch (e: Exception) {
            val response = ApiResponse<UserResponse>(
                success = false,
                message = "Sunucu tarafında beklenmedik bir hata oluştu."
            )
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        }
    }

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