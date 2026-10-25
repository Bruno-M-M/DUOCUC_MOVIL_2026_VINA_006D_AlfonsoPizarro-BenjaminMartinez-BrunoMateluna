package com.fundaciontextil

import android.app.Application
import android.content.pm.ApplicationInfo
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.local.DatosDePrueba

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TextilApp : Application() {

    private val ambito = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        ServiceLocator.iniciar(this)

        val esDepurable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (esDepurable) {
            ambito.launch { DatosDePrueba(ServiceLocator.usuarioDao).cargarSiHaceFalta() }
        }
    }
}