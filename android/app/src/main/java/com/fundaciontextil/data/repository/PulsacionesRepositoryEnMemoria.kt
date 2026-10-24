package com.fundaciontextil.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PulsacionesRepositoryEnMemoria : PulsacionesRepository {

    private val _pulsaciones = MutableStateFlow(0)
    override val pulsaciones: Flow<Int> = _pulsaciones.asStateFlow()

    override suspend fun sumar() {
        _pulsaciones.update { it + 1 }
    }
}