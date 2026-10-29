package com.fundaciontextil

import android.app.Application
import android.content.pm.ApplicationInfo
import com.fundaciontextil.data.ServiceLocator
import com.fundaciontextil.data.local.DatosDeCatalogo
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

        // El catálogo se carga siempre, pero solo si la tabla está vacía (primer arranque)
        ambito.launch { DatosDeCatalogo(ServiceLocator.catalogoDao).cargarSiHaceFalta() }

        // Borra fotos que ya no usa nadie: considera las del perfil y las de los productos.
        ambito.launch { ServiceLocator.limpiezaFotos.ejecutar() }

        val esDepurable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        if (esDepurable) {
            ambito.launch { DatosDePrueba(ServiceLocator.usuarioDao).cargarSiHaceFalta() }
        }
    }
}