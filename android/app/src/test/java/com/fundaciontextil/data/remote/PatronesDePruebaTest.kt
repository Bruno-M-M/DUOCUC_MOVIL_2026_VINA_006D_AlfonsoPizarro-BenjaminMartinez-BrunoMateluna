package com.fundaciontextil.data.remote

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PatronesDePruebaTest {

    @Test
    fun conMockkSimulaElApiService() = runTest {
        val api: ApiService = mockk()
        coEvery { api.emprendedoras() } returns listOf(
            EmprendedoraDto(1, "María Soto", "Tejidos Soto", "Viña del Mar"),
        )

        val lista = api.emprendedoras()

        assertEquals(1, lista.size)
        coVerify(exactly = 1) { api.emprendedoras() }
    }

    @Test
    fun conTurbineRevisaLosValoresDeUnFlow() = runTest {
        flowOf("cargando", "listo").test {
            assertEquals("cargando", awaitItem())
            assertEquals("listo", awaitItem())
            awaitComplete()
        }
    }
}