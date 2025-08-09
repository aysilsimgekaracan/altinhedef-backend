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
@Table(name = "student")
data class Student (
    @Id
    @Column(name = "user_id")
    val userId: Long,

    @Column(nullable = true)
    val golds: Int? = 0,

    @Column(name = "birth_date", nullable = true)
    val birthDate: Date?,

    @Column(name = "phone_number", length = 30, nullable = true)
    val phoneNumber: String?,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    val user: User? = null
)