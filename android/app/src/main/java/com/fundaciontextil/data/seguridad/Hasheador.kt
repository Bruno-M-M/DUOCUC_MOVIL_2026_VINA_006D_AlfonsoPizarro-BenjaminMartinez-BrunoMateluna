package com.fundaciontextil.data.seguridad

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Hasheador {

    private const val ALGORITMO = "PBKDF2WithHmacSHA256"
    private const val ITERACIONES = 120_000
    private const val LARGO_HASH_BITS = 256
    private const val LARGO_SAL_BYTES = 16

    //el RUT debe dar el mismo hash para poder detectar RUT repetidos, por eso se usa una sal fija.
    private const val SAL_RUT = "fundacion-textil-circular-rut-v1"

    fun generarSal(): String {
        val bytes = ByteArray(LARGO_SAL_BYTES)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun hashClave(clave: String, sal: String): String = pbkdf2(clave, sal.toByteArray())

    fun claveCoincide(clave: String, sal: String, hashGuardado: String): Boolean =
        MessageDigest.isEqual(hashClave(clave, sal).toByteArray(), hashGuardado.toByteArray())

    fun hashRut(rut: String): String = pbkdf2(normalizarRut(rut), SAL_RUT.toByteArray())

    fun normalizarRut(rut: String): String = rut.filter { it.isLetterOrDigit() }.uppercase()

    private fun pbkdf2(texto: String, sal: ByteArray): String {
        val especificaciones = PBEKeySpec(texto.toCharArray(), sal, ITERACIONES, LARGO_HASH_BITS)
        val bytes = SecretKeyFactory.getInstance(ALGORITMO).generateSecret(especificaciones).encoded
        especificaciones.clearPassword()
        return Base64.getEncoder().encodeToString(bytes)
    }
}