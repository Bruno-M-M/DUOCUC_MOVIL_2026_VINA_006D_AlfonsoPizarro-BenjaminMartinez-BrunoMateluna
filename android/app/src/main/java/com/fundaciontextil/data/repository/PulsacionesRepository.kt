package com.fundaciontextil.data.repository

import kotlinx.coroutines.flow.Flow

interface PulsacionesRepository {
    val pulsaciones: Flow<Int>

    suspend fun sumar()
}