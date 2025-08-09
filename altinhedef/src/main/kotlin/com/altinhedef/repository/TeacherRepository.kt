package com.altinhedef.repository

import com.altinhedef.entity.Teacher
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TeacherRepository: JpaRepository<Teacher, Long> {

}