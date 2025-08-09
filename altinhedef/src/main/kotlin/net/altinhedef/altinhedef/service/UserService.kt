package net.altinhedef.altinhedef.service

import jakarta.transaction.Transactional
import net.altinhedef.altinhedef.dto.auth.RegisterRequest
import net.altinhedef.altinhedef.dto.auth.CreateTeacherRequest
import net.altinhedef.altinhedef.entity.Role
import net.altinhedef.altinhedef.entity.Student
import net.altinhedef.altinhedef.entity.Teacher
import net.altinhedef.altinhedef.entity.User
import net.altinhedef.altinhedef.repository.RoleRepository
import net.altinhedef.altinhedef.repository.TeacherRepository
import net.altinhedef.altinhedef.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {
    @Transactional
    fun registerUser(request: RegisterRequest): User {
        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalStateException("Bu email adresi zaten kullanılıyor. Lütfen giriş yapınız.")
        }

        val studentRole = roleRepository.findByName("STUDENT")
            ?:throw  java.lang.IllegalStateException("STUDENT rolü veritabanında bulunmadı.")

        val hashedPassword = passwordEncoder.encode(request.password)

        val newUser = User(
            name = request.name,
            surname = request.surname,
            email = request.email,
            passwordHash = hashedPassword,
            role = studentRole
        )

        val newStudentProfile = Student(
            userId = 0,
            user = newUser
        )

        newUser.studentProfile = newStudentProfile

        return userRepository.save(newUser)
    }

    @Transactional
    fun createTeacher(request: CreateTeacherRequest): User {
        userRepository.findByEmail(request.email)?.let {
            throw IllegalStateException("Bu email adresi zaten kullanılıyor.")
        }

        val teacherRole = roleRepository.findByName("TEACHER") ?: throw IllegalStateException("Teacher rolü bulunamadı")

        val hashedPassword = passwordEncoder.encode(request.password)

        val newUser = User(
            name = request.name,
            surname = request.surname,
            email = request.email,
            passwordHash = hashedPassword,
            role = teacherRole
        )

        val savedUser = userRepository.save(newUser)

        val newTeacherProfile = Teacher(
            userId = 0,
            bio = request.bio,
            user = newUser
        )

        newUser.teacherProfile = newTeacherProfile

        return savedUser
    }

    fun findUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("Bu email ile bir kullanıcı bulunamadı: $email")
    }

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("Kullanıcı bulunamadı: $username")
    }
}