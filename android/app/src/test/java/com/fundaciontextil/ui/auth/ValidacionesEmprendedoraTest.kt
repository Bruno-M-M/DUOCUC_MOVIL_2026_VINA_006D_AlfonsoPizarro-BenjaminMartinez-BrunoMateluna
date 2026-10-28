package com.fundaciontextil.ui.auth

import com.fundaciontextil.data.model.CATEGORIAS_SERVICIO
import com.fundaciontextil.data.model.RegionesDeChile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesEmprendedoraTest {

    private val reparacion = CATEGORIAS_SERVICIO[2]
    private val supraReciclaje = CATEGORIAS_SERVICIO[3]
    private val tenido = CATEGORIAS_SERVICIO[5]

    @Test
    fun `hay 16 regiones y 7 categorias, sin repetidas`() {
        assertEquals(16, RegionesDeChile.regiones.size)
        assertEquals(16, RegionesDeChile.regiones.map { it.nombre }.toSet().size)
        assertEquals(7, CATEGORIAS_SERVICIO.toSet().size)
        assertTrue(RegionesDeChile.regiones.all { it.comunas.isNotEmpty() })
    }

    @Test
    fun `las comunas dependen de la region`() {
        assertTrue("Viña del Mar" in RegionesDeChile.comunasDe("Valparaíso"))
        assertTrue("Viña del Mar" !in RegionesDeChile.comunasDe("Biobío"))
        assertTrue(RegionesDeChile.comunasDe(null).isEmpty())
    }

    @Test
    fun `el nombre comercial es obligatorio`() {
        assertEquals("Ingresa el nombre de tu emprendimiento", validarNombreComercial(" "))
        assertEquals("El nombre del emprendimiento es muy corto", validarNombreComercial("A"))
        assertNull(validarNombreComercial("Telar Verde"))
    }

    @Test
    fun `la region y la comuna se eligen y la comuna debe ser de esa region`() {
        assertEquals("Elige tu región", validarRegion(null))
        assertNull(validarRegion("Valparaíso"))
        assertEquals("Elige tu comuna", validarComuna("Valparaíso", null))
        assertEquals("Elige una comuna de tu región", validarComuna("Biobío", "Viña del Mar"))
        assertNull(validarComuna("Valparaíso", "Viña del Mar"))
    }

    @Test
    fun `se pide al menos una y como maximo dos categorias`() {
        assertEquals("Elige al menos una categoría", validarCategorias(emptySet()))
        assertNull(validarCategorias(setOf(reparacion)))
        assertNull(validarCategorias(setOf(reparacion, tenido)))
        assertEquals(
            "Puedes elegir hasta 2 categorías",
            validarCategorias(setOf(reparacion, tenido, supraReciclaje)),
        )
    }

    @Test
    fun `la tercera categoria se bloquea y desmarcar siempre se puede`() {
        val dos = setOf(reparacion, tenido)

        assertEquals(setOf(reparacion), alternarCategoria(emptySet(), reparacion))
        assertNull(alternarCategoria(dos, supraReciclaje))
        assertEquals(setOf(tenido), alternarCategoria(dos, reparacion))
    }
}