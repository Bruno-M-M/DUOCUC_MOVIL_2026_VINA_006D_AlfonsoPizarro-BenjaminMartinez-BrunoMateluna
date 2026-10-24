package com.fundaciontextil.ui.ejemplo

import com.fundaciontextil.data.repository.PulsacionesRepositoryEnMemoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EjemploViewModelTest {

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al pulsar sumar aumenta las pulsaciones`() = runTest {
        val viewModel = EjemploViewModel(PulsacionesRepositoryEnMemoria())
        val estados = mutableListOf<Int>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { estados += it.pulsaciones }
        }

        viewModel.alPulsarSumar()
        viewModel.alPulsarSumar()

        assertEquals(listOf(0, 1, 2), estados)
        job.cancel()
    }
}
