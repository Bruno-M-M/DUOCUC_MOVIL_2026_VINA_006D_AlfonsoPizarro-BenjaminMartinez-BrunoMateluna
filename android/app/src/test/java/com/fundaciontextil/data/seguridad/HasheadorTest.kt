package com.fundaciontextil.data.seguridad

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HasheadorTest {

    @Test
    fun `la misma clave con la misma sal da el mismo hash`() {
        val sal = Hasheador.generarSal()

        assertEquals(Hasheador.hashClave("hola1234", sal), Hasheador.hashClave("hola1234", sal))
    }

    @Test
    fun `la misma clave con sales distintas da hashes distintos`() {
        val hashAna = Hasheador.hashClave("hola1234", Hasheador.generarSal())
        val hashLuis = Hasheador.hashClave("hola1234", Hasheador.generarSal())

        assertNotEquals(hashAna, hashLuis)
    }

    @Test
    fun `el hash no contiene la clave en claro`() {
        val hash = Hasheador.hashClave("hola1234", Hasheador.generarSal())

        assertFalse(hash.contains("hola1234"))
    }

    @Test
    fun `la clave correcta coincide y una incorrecta no`() {
        val sal = Hasheador.generarSal()
        val hash = Hasheador.hashClave("hola1234", sal)

        assertTrue(Hasheador.claveCoincide("hola1234", sal, hash))
        assertFalse(Hasheador.claveCoincide("hola12345", sal, hash))
    }

    @Test
    fun `cada sal generada es distinta`() {
        assertNotEquals(Hasheador.generarSal(), Hasheador.generarSal())
    }

    @Test
    fun `el hash del rut no depende de puntos, guion ni mayusculas`() {
        val esperado = Hasheador.hashRut("12.345.678-K")

        assertEquals(esperado, Hasheador.hashRut("12345678-k"))
        assertEquals(esperado, Hasheador.hashRut("12345678K"))
    }

    @Test
    fun `dos rut distintos dan hashes distintos`() {
        assertNotEquals(Hasheador.hashRut("12.345.678-5"), Hasheador.hashRut("12.345.679-3"))
    }
}
