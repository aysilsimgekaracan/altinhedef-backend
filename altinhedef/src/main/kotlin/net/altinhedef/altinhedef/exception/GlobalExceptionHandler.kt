package net.altinhedef.altinhedef.exception

import net.altinhedef.altinhedef.dto.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Map<String, String?>>> {
        val errors = ex.bindingResult.fieldErrors.associate { error ->
            error.field to error.defaultMessage
        }

        val errorResponse = ApiResponse(
            success = false,
            message = "Girilen bilgilerde hatalar mevcut.",
            data = errors
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }
}