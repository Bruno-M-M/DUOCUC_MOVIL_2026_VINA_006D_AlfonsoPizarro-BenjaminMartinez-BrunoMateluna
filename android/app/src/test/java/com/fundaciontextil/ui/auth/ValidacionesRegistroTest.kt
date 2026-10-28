package com.fundaciontextil.ui.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesRegistroTest {

    @Test
    fun `el nombre es obligatorio, completo y solo con letras`() {
        assertEquals("Ingresa tu nombre", validarNombre("  "))
        assertEquals("Escribe tu nombre completo", validarNombre("Al"))
        assertEquals("El nombre solo puede tener letras", validarNombre("Ana 123"))
        assertNull(validarNombre("María José O'Higgins"))
    }

    @Test
    fun `el digito verificador se calcula con modulo 11`() {
        assertEquals('5', digitoVerificador("12345678"))
        assertEquals('1', digitoVerificador("11111111"))
        assertEquals('K', digitoVerificador("10000013"))
        assertEquals('0', digitoVerificador("10000004"))
    }

    @Test
    fun `un RUT correcto es valido con o sin puntos y guion`() {
        assertNull(validarRut("12.345.678-5"))
        assertNull(validarRut("12345678-5"))
        assertNull(validarRut("123456785"))
        assertNull(validarRut("10.000.013-k"))
        assertNull(validarRut("9.999.999-3"))
    }

    @Test
    fun `un RUT vacio, mal escrito o con otro digito verificador no es valido`() {
        assertEquals("Ingresa tu RUT", validarRut(""))
        assertEquals("Ingresa un RUT válido, por ejemplo 12.345.678-5", validarRut("12.345"))
        assertEquals("Ingresa un RUT válido, por ejemplo 12.345.678-5", validarRut("abc"))
        assertEquals("El RUT no es válido", validarRut("12.345.678-9"))
    }

    @Test
    fun `la clave nueva debe tener al menos 8 caracteres con letras y numeros`() {
        assertEquals("Ingresa una contraseña", validarClaveNueva(""))
        assertEquals("Usa al menos 8 caracteres", validarClaveNueva("abc123"))
        assertEquals("Usa letras y números", validarClaveNueva("abcdefgh"))
        assertEquals("Usa letras y números", validarClaveNueva("12345678"))
        assertNull(validarClaveNueva("telar2030"))
    }

    @Test
    fun `repetir la clave debe coincidir con la primera`() {
        assertEquals("Repite la contraseña", validarRepetirClave("telar2030", ""))
        assertEquals("Las contraseñas no coinciden", validarRepetirClave("telar2030", "telar2031"))
        assertNull(validarRepetirClave("telar2030", "telar2030"))
    }
}