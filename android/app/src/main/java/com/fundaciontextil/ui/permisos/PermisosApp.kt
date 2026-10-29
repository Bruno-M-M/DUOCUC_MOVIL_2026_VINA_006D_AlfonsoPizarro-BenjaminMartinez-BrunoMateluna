
package com.fundaciontextil.ui.permisos

import android.Manifest

object PermisosApp {

    val CAMARA = listOf(
        Manifest.permission.CAMERA
    )

    val UBICACION = listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
}
