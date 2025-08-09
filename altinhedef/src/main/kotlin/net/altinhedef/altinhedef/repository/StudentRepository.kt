package net.altinhedef.altinhedef.repository

import net.altinhedef.altinhedef.entity.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : JpaRepository<Student, Long> {
}