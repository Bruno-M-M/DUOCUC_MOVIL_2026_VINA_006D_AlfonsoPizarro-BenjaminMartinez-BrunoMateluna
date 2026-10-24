package com.fundaciontextil.data

import com.fundaciontextil.data.repository.PulsacionesRepository
import com.fundaciontextil.data.repository.PulsacionesRepositoryEnMemoria

object ServiceLocator {

    val pulsacionesRepository: PulsacionesRepository by lazy {
        PulsacionesRepositoryEnMemoria()
    }
}
