package com.es.sessionsecurity.service

import com.es.sessionsecurity.error.exception.ForbiddenException
import com.es.sessionsecurity.error.exception.NotAuthorizedException
import com.es.sessionsecurity.error.exception.NotFoundException
import com.es.sessionsecurity.model.Reserva
import com.es.sessionsecurity.model.Rol
import com.es.sessionsecurity.repository.ReservaRepository
import com.es.sessionsecurity.repository.UsuarioRepository
import com.es.sessionsecurity.util.CipherUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReservaService {

    @Autowired
    private lateinit var reservaRepository: ReservaRepository

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository

    @Autowired
    private lateinit var usuarioService: UsuarioService

    private val cipher = CipherUtils()


    fun crearReserva(reserva: Reserva, token: String?): Reserva {
        if (token.isNullOrBlank()) {
            throw NotAuthorizedException("Token ausente")
        }

        val key = cipher.key
        val nombreUsuario = try {
            cipher.decrypt(token, key)
        } catch (e: Exception) {
            throw NotAuthorizedException("Token inválido")
        }

        val usuario = usuarioService.findByNombre(nombreUsuario)
            ?: throw NotAuthorizedException("Usuario no encontrado")

        if (usuario.rol != Rol.ADMIN) {
            throw ForbiddenException("No tiene permisos para realizar esta operación")
        }

        return reservaRepository.save(reserva)
    }

    fun eliminarReserva(reservaId: Long, userId: Long) {
        val reserva: Reserva = reservaRepository.findById(reservaId)
            .orElseThrow { NotFoundException("Reserva con ID $reservaId no encontrada") }

        if (reserva.usuario.id != userId) {
            throw NotAuthorizedException("No tienes permiso para eliminar esta reserva")
        }

        reservaRepository.delete(reserva)
    }

    fun getReservasByNombre(nombre: String, token: String?): List<Reserva> {
        if (token.isNullOrBlank()) {
            throw NotAuthorizedException("Token ausente")
        }

        val key = cipher.key
        val nombreUsuario = try {
            cipher.decrypt(token, key)
        } catch (e: Exception) {
            throw NotAuthorizedException("Token no válido")
        }

        val usuario = usuarioService.findByNombre(nombreUsuario)
            ?: throw NotAuthorizedException("Usuario no encontrado")

        println(nombre)
        println(nombreUsuario)
        println(usuario.nombre)
        println(usuario.rol)

        return when {
            usuario.rol == Rol.ADMIN -> reservaRepository.findByUsuario_Nombre(nombre)
            usuario.nombre == nombre -> reservaRepository.findByUsuario_Nombre(nombre)
            else -> throw ForbiddenException("No tiene permisos para ver estas reservas")
        }
    }
}