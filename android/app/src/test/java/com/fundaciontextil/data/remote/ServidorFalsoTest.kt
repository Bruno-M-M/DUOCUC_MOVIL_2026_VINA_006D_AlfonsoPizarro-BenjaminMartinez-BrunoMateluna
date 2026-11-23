package com.fundaciontextil.data.remote

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ServidorFalsoTest {

    private val servidor = ServidorFalso(demora = 0L)

    @Test
    fun devuelveLasEmprendedoras() = runTest {
        assertEquals(2, servidor.emprendedoras().size)
    }

    @Test
    fun filtraLosProductosPorEmprendedora() = runTest {
        val productos = servidor.productosDe(1L)
        assertEquals(2, productos.size)
        assertEquals(listOf(1L, 1L), productos.map { it.emprendedoraId })
    }

    @Test
    fun emprendedoraSinProductosDevuelveListaVacia() = runTest {
        assertEquals(emptyList<ProductoDto>(), servidor.productosDe(99L))
    }
}