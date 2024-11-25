package com.es.sessionsecurity.controller

import com.es.sessionsecurity.error.exception.ForbiddenException
import com.es.sessionsecurity.error.exception.NotAuthorizedException
import com.es.sessionsecurity.error.exception.NotFoundException
import com.es.sessionsecurity.model.Reserva
import com.es.sessionsecurity.model.Rol
import com.es.sessionsecurity.model.Usuario
import com.es.sessionsecurity.repository.ReservaRepository
import com.es.sessionsecurity.repository.UsuarioRepository
import com.es.sessionsecurity.service.ReservaService
import com.es.sessionsecurity.service.SessionService
import com.es.sessionsecurity.service.UsuarioService
import com.es.sessionsecurity.util.CipherUtils
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reservas")
class ReservaController {

    @Autowired
    private lateinit var sessionService: SessionService

    @Autowired
    private lateinit var reservaService: ReservaService

    /*
    OBTENER TODAS LAS RESERVAS POR EL NOMBRE DE USUARIO DE UN CLIENTE
     */
    @GetMapping("/{nombre}")
    fun getByNombreUsuario(
        @PathVariable nombre: String,
        request: HttpServletRequest
    ) : ResponseEntity<List<Reserva>?> {

        val cookie: Cookie? = request.cookies.find{ c: Cookie? -> c?.name == "tokenSession"}
        val token: String? = cookie?.value

        val reservas = reservaService.getReservasByNombre(nombre, token)

        return ResponseEntity(reservas, HttpStatus.OK)
    }

    @PostMapping("/")
    fun insert(
        @RequestBody nuevaReserva: Reserva,
        request: HttpServletRequest
    ): ResponseEntity<Reserva?> {
        val cookie: Cookie? = request.cookies?.find { it.name == "tokenSession" }
        val token: String? = cookie?.value

        val reserva = reservaService.crearReserva(nuevaReserva, token)

        return ResponseEntity(reserva, HttpStatus.CREATED)
    }
}