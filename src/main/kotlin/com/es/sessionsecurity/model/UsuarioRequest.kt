package com.es.sessionsecurity.model

data class UsuarioRequest(
    val nombre: String,
    val password: String,
    var rol: Rol = Rol.USER
)