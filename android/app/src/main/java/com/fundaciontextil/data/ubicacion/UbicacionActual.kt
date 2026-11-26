package com.fundaciontextil.data.ubicacion

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class UbicacionActual(private val context: Context) {

    // Llamar solo después de que la persona concedió ACCESS_COARSE_LOCATION.
    @SuppressLint("MissingPermission")
    suspend fun obtener(): Coordenada? {
        val gestor = context.getSystemService(LocationManager::class.java)
        val proveedor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            LocationManager.FUSED_PROVIDER
        } else {
            LocationManager.NETWORK_PROVIDER
        }
        if (proveedor !in gestor.allProviders || !gestor.isProviderEnabled(proveedor)) return null
        return suspendCancellableCoroutine { continuacion ->
            val cancelar = CancellationSignal()
            continuacion.invokeOnCancellation { cancelar.cancel() }
            LocationManagerCompat.getCurrentLocation(
                gestor,
                proveedor,
                cancelar,
                ContextCompat.getMainExecutor(context),
            ) { lugar -> continuacion.resume(lugar?.let { Coordenada(it.latitude, it.longitude) }) }
        }
    }
}
