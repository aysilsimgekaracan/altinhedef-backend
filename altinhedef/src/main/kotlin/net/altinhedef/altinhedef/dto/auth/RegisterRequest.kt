package net.altinhedef.altinhedef.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "İsim boş olamaz")
    val name: String,

    @field:NotBlank(message = "Soyisim boş olamaz")
    val surname: String,

    @field:Email(message = "Lütfen geçerli bir e-posta adresi giriniz.")
    @field:NotBlank
    val email: String,

    @field:Size(min = 8, message = "Şifre en az 8 karakter olmalıdır.")
    @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
        message = "Şifre en az bir rakam, bir küçük harf, bir büyük harf ve bir özel karakter (@#$%^&+=!) içermelidir"
    )
    val password: String
)
