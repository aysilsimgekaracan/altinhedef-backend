package net.altinhedef.altinhedef.repository

import net.altinhedef.altinhedef.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): User?

    fun existsByRole_Name(roleName: String): Boolean
}