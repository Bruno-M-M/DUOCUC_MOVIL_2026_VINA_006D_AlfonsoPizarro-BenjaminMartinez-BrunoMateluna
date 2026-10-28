package com.fundaciontextil.data.repository

import com.fundaciontextil.data.local.EmprendedoraEntity
import com.fundaciontextil.data.local.ProductoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogoRepositoryLocalTest {

    private val dao = CatalogoDaoFalso()
    private val repositorio = CatalogoRepositoryLocal(dao)

    @Test
    fun `sin datos la lista de emprendedoras esta vacia`() = runTest {
        assertTrue(repositorio.emprendedoras.first().isEmpty())
    }

    @Test
    fun `al insertar una emprendedora el flow emite la lista actualizada`() = runTest {
        dao.insertarEmprendedora(EmprendedoraEntity(nombre = "Ana", emprendimiento = "Retazos", comuna = "Ñuñoa"))

        val lista = repositorio.emprendedoras.first()

        assertEquals(1, lista.size)
        assertEquals("Ana", lista[0].nombre)
    }

    @Test
    fun `productosDe devuelve solo los productos de esa emprendedora`() = runTest {
        val ana = dao.insertarEmprendedora(EmprendedoraEntity(nombre = "Ana", emprendimiento = "A", comuna = "X"))
        val marta = dao.insertarEmprendedora(EmprendedoraEntity(nombre = "Marta", emprendimiento = "M", comuna = "Y"))
        dao.insertarProducto(ProductoEntity(emprendedoraId = ana, nombre = "Bolso", descripcion = "d", precio = 10000))
        dao.insertarProducto(ProductoEntity(emprendedoraId = marta, nombre = "Chal", descripcion = "d", precio = 20000))

        val deAna = repositorio.productosDe(ana).first()

        assertEquals(1, deAna.size)
        assertEquals("Bolso", deAna[0].nombre)
    }
}