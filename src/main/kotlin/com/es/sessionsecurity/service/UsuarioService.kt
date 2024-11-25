package com.es.sessionsecurity.service

import com.es.sessionsecurity.error.exception.BadRequestException
import com.es.sessionsecurity.error.exception.NotFoundException
import com.es.sessionsecurity.model.Session
import com.es.sessionsecurity.model.Usuario
import com.es.sessionsecurity.model.UsuarioRequest
import com.es.sessionsecurity.repository.SessionRepository
import com.es.sessionsecurity.repository.UsuarioRepository
import com.es.sessionsecurity.util.CipherUtils
import jakarta.persistence.*
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@Service
class UsuarioService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository
    @Autowired
    private lateinit var sessionRepository: SessionRepository
    private val cipher = CipherUtils()

    fun login(userLogin: Usuario) : String {

        if(userLogin.password.isBlank() || userLogin.nombre.isBlank()) {
            throw BadRequestException("Los campos nombre y password son obligatorios")
        }

        var userBD: Usuario = usuarioRepository
            .findByNombre(userLogin.nombre)
            .orElseThrow{NotFoundException("El usuario proporcionado no existe en BDD")}

        if(userBD.password == cipher.encrypt(userLogin.password, cipher.key)) {

            var token: String = ""
            token = cipher.encrypt(userLogin.nombre, cipher.key)

            val s: Session = Session(
                null,
                token,
                userBD,
                LocalDateTime.now().plusMinutes(3)
            )

            sessionRepository.save(s)

            return token
        } else {
            throw NotFoundException("Las credenciales son incorrectas")
        }
    }

    fun crearUsuario(nuevoUsuario: UsuarioRequest): Usuario {

        if (nuevoUsuario.nombre.isBlank() || nuevoUsuario.password.isBlank()) {
            throw BadRequestException("Los campos nombre y password son obligatorios")
        }

        if (usuarioRepository.findByNombre(nuevoUsuario.nombre).isPresent) {
            throw BadRequestException("Ya existe un usuario con el nombre '${nuevoUsuario.nombre}'")
        }

        val passwordCifrada = cipher.encrypt(nuevoUsuario.password, cipher.key)

        return usuarioRepository.save(
            Usuario(
                null,
                nuevoUsuario.nombre,
                passwordCifrada,
                nuevoUsuario.rol
            )
        )
    }

    fun findByNombre(nombre: String): Usuario? {
        return usuarioRepository.findByNombre(nombre).orElse(null)
    }
}