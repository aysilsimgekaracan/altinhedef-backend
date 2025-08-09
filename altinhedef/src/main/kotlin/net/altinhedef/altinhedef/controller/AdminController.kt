package net.altinhedef.altinhedef.controller

import net.altinhedef.altinhedef.dto.ApiResponse
import net.altinhedef.altinhedef.dto.auth.CreateTeacherRequest
import net.altinhedef.altinhedef.dto.response.user.UserResponse
import net.altinhedef.altinhedef.entity.User
import net.altinhedef.altinhedef.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(private val userService: UserService) {

    @PostMapping("/register/teacher")
    fun createTeacher(@RequestBody request: CreateTeacherRequest): ResponseEntity<ApiResponse<UserResponse>> {
        return try {
            val newTeacher = userService.createTeacher(request)
            val response = ApiResponse(
                success = true,
                message = "Öğretmen başarıyla oluşturuldu.",
                data = newTeacher.toUserResponse()
            )
            ResponseEntity.status(HttpStatus.CREATED).body(response)
        } catch (e: IllegalStateException) {
            val errorResponse = ApiResponse<UserResponse>(
                success = false,
                message = e.message ?: "Bir hata oluştu."
            )
            ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
        } catch (e: Exception) {
            val errorResponse = ApiResponse<UserResponse>(
                success = false,
                message = e.message ?: "Bir hata oluştu."
            )
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
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