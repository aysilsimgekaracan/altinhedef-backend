package net.altinhedef.altinhedef.repository

import net.altinhedef.altinhedef.entity.Teacher
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TeacherRepository: JpaRepository<Teacher, Long> {

}