package com.es.sessionsecurity.util

import org.springframework.stereotype.Component
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import java.util.Base64

@Component
class CipherUtils {

    /**
     * Método para cifrar una cadena usando un cifrado por clave simétrica
     * La key proporcionada por parámetros debe ser la misma que se vaya a usar a la hora de descifrar
     */
    fun encrypt(cadenaACifrar: String, key: String): String {
        // CON LA KEY PROPORCIONADA SE GENERA UNA CLAVE PÚBLICA
        val keyBytes = key.toByteArray(Charsets.UTF_8).copyOf(16) // AES necesita claves de 16, 24 o 32 bytes
        val secretKey = SecretKeySpec(keyBytes, "AES")

        // CON ESA CLAVE PÚBLICA SE PUEDE CIFRAR LA CADENA
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding") // Modo CBC con relleno PKCS5
        val iv = IvParameterSpec(ByteArray(16)) // Vector de inicialización fijo (NO recomendado para datos críticos)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv)
        val cipherText = cipher.doFinal(cadenaACifrar.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(cipherText)
    }

    /**
     * Método para descifrar una cadena usando un cifrado por clave simétrica
     * La key proporcionada por parámetros debe ser la misma que se usó para cifrar la cadena
     */
    fun decrypt(cadenaCifrada: String, key: String): String {

        // CON LA KEY PROPORCIONADA SE GENERA UNA CLAVE PÚBLICA
        val keyBytes = key.toByteArray(Charsets.UTF_8).copyOf(16) // AES necesita claves de 16, 24 o 32 bytes
        val secretKey = SecretKeySpec(keyBytes, "AES")

        // CON ESA CLAVE PÚBLICA SE PUEDE DESCIFRAR EL MENSAJE
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = IvParameterSpec(ByteArray(16)) // Mismo vector de inicialización que en el cifrado
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)
        val plainText = cipher.doFinal(Base64.getDecoder().decode(cadenaCifrada))
        return String(plainText, Charsets.UTF_8)
    }

    val key = "3e9378ec4c7645f0f6cd9690d7c56184"
}