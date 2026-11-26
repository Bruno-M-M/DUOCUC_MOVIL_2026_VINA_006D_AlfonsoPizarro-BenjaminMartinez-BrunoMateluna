package com.fundaciontextil.data.externo

import com.fundaciontextil.data.remote.ResultadoRemoto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException

class DolarRepositoryTest {

    private val api: IndicadoresService = mockk()
    private val repositorio = DolarRepository(api)

    @Test
    fun `devuelve el primer valor de la serie`() = runTest {
        coEvery { api.dolar() } returns IndicadorDto(
            "dolar",
            listOf(ValorDto("2026-11-24", 950.0), ValorDto("2026-11-23", 940.0)),
        )
        assertEquals(ResultadoRemoto.Exito(950.0), repositorio.valorDeHoy())
    }

    @Test
    fun `sin red avisa que no hay conexion`() = runTest {
        coEvery { api.dolar() } throws IOException("sin red")
        assertEquals(ResultadoRemoto.SinConexion, repositorio.valorDeHoy())
    }

    @Test
    fun `una serie vacia es un error del servidor`() = runTest {
        coEvery { api.dolar() } returns IndicadorDto("dolar", emptyList())
        assertEquals(ResultadoRemoto.ErrorServidor(200, "SIN_DATOS"), repositorio.valorDeHoy())
    }
}
