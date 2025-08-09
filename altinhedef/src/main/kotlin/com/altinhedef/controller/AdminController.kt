package com.altinhedef.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.responses.ApiResponse as OpenApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import com.altinhedef.dto.ApiResponse
import com.altinhedef.dto.auth.CreateTeacherRequest
import com.altinhedef.dto.response.user.UserResponse
import com.altinhedef.entity.User
import com.altinhedef.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin Operations", description = "Operations that only admin users can request")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(private val userService: UserService) {

    @Operation(
        summary = "Register New Teacher",
        description = "It creates a new user with 'TEACHER' role",
        security = [SecurityRequirement(name = "Bearer Authentication")],
        responses = [
            OpenApiResponse(responseCode = "201", description = "Created new teacher successfully."),
            OpenApiResponse(responseCode = "409", description = "This mail address is already in use."),
            OpenApiResponse(responseCode = "403", description = "You don't have access. (Forbidden)")
        ]
    )
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