package net.altinhedef.altinhedef.repository

import net.altinhedef.altinhedef.entity.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository: JpaRepository<Role, Int> {
    fun findByName(name: String): Role?
}