package com.fundaciontextil.ui.producto

import app.cash.turbine.test
import com.fundaciontextil.MainDispatcherRule
import com.fundaciontextil.data.externo.DolarRepository
import com.fundaciontextil.data.remote.ResultadoRemoto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class DolarViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repositorio: DolarRepository = mockk()

    @Test
    fun `pasa de cargando al valor del dolar`() = runTest {
        val respuesta = CompletableDeferred<ResultadoRemoto<Double>>()
        coEvery { repositorio.valorDeHoy() } coAnswers { respuesta.await() }

        DolarViewModel(repositorio).dolar.test {
            assertNull(awaitItem())
            respuesta.complete(ResultadoRemoto.Exito(1000.0))
            assertEquals(ResultadoRemoto.Exito(1000.0), awaitItem())
        }
    }

    @Test
    fun `convierte el precio a dolares`() {
        assertEquals("≈ US$ 22.00", precioEnDolares(22000, 1000.0))
    }

    @Test
    fun `cada resultado tiene su mensaje`() {
        assertEquals("Cargando el valor del dólar…", mensajeDolar(null))
        assertEquals("Sin conexión: mostramos solo pesos", mensajeDolar(ResultadoRemoto.SinConexion))
        assertEquals("El valor del dólar no está disponible", mensajeDolar(ResultadoRemoto.ErrorServidor(500, null)))
    }
}
