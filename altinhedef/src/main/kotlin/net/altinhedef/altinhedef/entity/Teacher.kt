package net.altinhedef.altinhedef.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "teachers")
data class Teacher (
    @Id
    @Column(name = "user_id")
    val userId: Long,

    @Column(columnDefinition = "TEXT")
    var bio: String? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    val user: User? = null
)