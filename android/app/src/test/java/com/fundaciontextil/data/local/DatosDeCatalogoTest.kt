package com.fundaciontextil.data.local

import com.fundaciontextil.data.repository.CatalogoDaoFalso
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DatosDeCatalogoTest {

    private val dao = CatalogoDaoFalso()
    private val semilla = DatosDeCatalogo(dao)

    @Test
    fun `carga emprendedoras y productos`() = runTest {
        semilla.cargarSiHaceFalta()

        assertEquals(3, dao.emprendedorasFlow.value.size)
        assertEquals(6, dao.productosFlow.value.size)
    }

    @Test
    fun `cargarla dos veces no duplica los datos`() = runTest {
        semilla.cargarSiHaceFalta()
        semilla.cargarSiHaceFalta()

        assertEquals(3, dao.emprendedorasFlow.value.size)
    }

    @Test
    fun `cada producto pertenece a una emprendedora existente`() = runTest {
        semilla.cargarSiHaceFalta()
        val ids = dao.emprendedorasFlow.value.map { it.id }.toSet()

        assertTrue(dao.productosFlow.value.all { it.emprendedoraId in ids })
    }
}