package com.es.sessionsecurity.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "sessions")
data class Session(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?,
    var token: String,
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    var usuario: Usuario,
    var fechaExp: LocalDateTime
)