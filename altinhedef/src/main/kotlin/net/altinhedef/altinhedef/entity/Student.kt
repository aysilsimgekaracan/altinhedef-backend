package net.altinhedef.altinhedef.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.sql.Date

@Entity
@Table(name = "students")
data class Student (
    @Id
    @Column(name = "user_id")
    val userId: Long,

    @Column(nullable = false)
    var golds: Int = 0,

    @Column(name = "birth_date", nullable = true)
    var birthDate: Date? = null,

    @Column(name = "phone_number", length = 30, nullable = true)
    var phoneNumber: String? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    val user: User
)