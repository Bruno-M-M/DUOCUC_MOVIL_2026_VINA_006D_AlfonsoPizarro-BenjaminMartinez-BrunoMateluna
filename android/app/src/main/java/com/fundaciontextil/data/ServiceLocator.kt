package com.fundaciontextil.data

import android.content.Context
import com.fundaciontextil.data.local.AppDatabase
import com.fundaciontextil.data.local.UsuarioDao
import com.fundaciontextil.data.repository.PulsacionesRepository
import com.fundaciontextil.data.repository.PulsacionesRepositoryEnMemoria

object ServiceLocator {

    private lateinit var contexto: Context

    fun iniciar(context: Context) {
        contexto = context.applicationContext
    }

    private val baseDeDatos: AppDatabase by lazy { AppDatabase.crear(contexto) }

    val usuarioDao: UsuarioDao by lazy { baseDeDatos.usuarioDao() }

    val pulsacionesRepository: PulsacionesRepository by lazy {
        PulsacionesRepositoryEnMemoria()
    }
}