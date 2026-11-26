package com.fundaciontextil.ui.explorar

import app.cash.turbine.test
import com.fundaciontextil.MainDispatcherRule
import com.fundaciontextil.data.model.Emprendedora
import com.fundaciontextil.data.repository.CatalogoRepository
import com.fundaciontextil.data.ubicacion.Coordenada
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ExplorarViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repositorio: CatalogoRepository = mockk()
    private val ana = Emprendedora(1, "Ana Rojas", "Retazos Ana", "Ñuñoa")
    private val rosa = Emprendedora(2, "Rosa Díaz", "Hilos Rosa", "Viña del Mar")

    private fun conDatos(): ExplorarViewModel {
        every { repositorio.emprendedoras } returns flowOf(listOf(ana, rosa))
        return ExplorarViewModel(repositorio)
    }

    @Test
    fun `pasa de cargando a mostrar la lista`() = runTest {
        val emisor = MutableSharedFlow<List<Emprendedora>>()
        every { repositorio.emprendedoras } returns emisor

        ExplorarViewModel(repositorio).uiState.test {
            assertTrue(awaitItem().cargando)
            emisor.emit(listOf(ana))
            val listo = awaitItem()
            assertFalse(listo.cargando)
            assertEquals(listOf(ana), listo.visibles)
        }
    }

    @Test
    fun `si la carga falla muestra el error y al reintentar lo borra`() {
        every { repositorio.emprendedoras } returns flow { throw IOException("sin red") }
        val viewModel = ExplorarViewModel(repositorio)
        assertEquals(ExplorarViewModel.MENSAJE_ERROR, viewModel.uiState.value.error)

        every { repositorio.emprendedoras } returns flowOf(listOf(ana))
        viewModel.cargar()
        assertNull(viewModel.uiState.value.error)
        assertEquals(listOf(ana), viewModel.uiState.value.visibles)
    }

    @Test
    fun `filtra por region`() {
        val viewModel = conDatos()
        viewModel.alElegirRegion("Valparaíso")
        assertEquals(listOf(rosa), viewModel.uiState.value.visibles)
    }

    @Test
    fun `al cambiar la region se borra la comuna`() {
        val viewModel = conDatos()
        viewModel.alElegirRegion("Valparaíso")
        viewModel.alElegirComuna("Viña del Mar")
        viewModel.alElegirRegion("Metropolitana de Santiago")
        assertNull(viewModel.uiState.value.comuna)
        assertEquals(listOf(ana), viewModel.uiState.value.visibles)
    }

    @Test
    fun `una comuna sin emprendedoras deja la lista vacia y limpiar la recupera`() {
        val viewModel = conDatos()
        viewModel.alElegirRegion("Valparaíso")
        viewModel.alElegirComuna("Quilpué")
        assertTrue(viewModel.uiState.value.visibles.isEmpty())
        viewModel.limpiarFiltros()
        assertEquals(listOf(ana, rosa), viewModel.uiState.value.visibles)
        assertFalse(viewModel.uiState.value.hayFiltros)
    }

    @Test
    fun `con ubicacion ordena por cercania y avisa que es aproximada`() {
        val viewModel = conDatos()
        viewModel.alObtenerUbicacion(Coordenada(-33.03, -71.55))
        assertEquals(listOf(rosa, ana), viewModel.uiState.value.visibles)
        assertEquals(ExplorarViewModel.MENSAJE_APROXIMADA, viewModel.uiState.value.avisoUbicacion)
    }

    @Test
    fun `sin ubicacion mantiene el orden y avisa`() {
        val viewModel = conDatos()
        viewModel.alObtenerUbicacion(null)
        assertEquals(listOf(ana, rosa), viewModel.uiState.value.visibles)
        assertEquals(ExplorarViewModel.MENSAJE_SIN_UBICACION, viewModel.uiState.value.avisoUbicacion)
    }

}
