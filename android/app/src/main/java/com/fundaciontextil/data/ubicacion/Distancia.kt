package com.fundaciontextil.data.ubicacion

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class Coordenada(val latitud: Double, val longitud: Double)

// Centro aproximado de cada comuna. Puede ser parcial: las que falten quedan al final de la lista.
val CENTRO_DE_COMUNA: Map<String, Coordenada> = mapOf(
    "Maipú" to Coordenada(-33.51, -70.76),
    "Ñuñoa" to Coordenada(-33.46, -70.60),
    "La Florida" to Coordenada(-33.52, -70.60),
    "Santiago" to Coordenada(-33.45, -70.67),
    "Viña del Mar" to Coordenada(-33.02, -71.55),
    "Valparaíso" to Coordenada(-33.05, -71.61),
)

fun distanciaKm(a: Coordenada, b: Coordenada): Double {
    val dLat = Math.toRadians(b.latitud - a.latitud)
    val dLon = Math.toRadians(b.longitud - a.longitud)
    val h = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(a.latitud)) * cos(Math.toRadians(b.latitud)) * sin(dLon / 2).pow(2)
    return 2 * 6371.0 * asin(sqrt(h))
}

fun distanciaAComuna(desde: Coordenada, comuna: String): Double? =
    CENTRO_DE_COMUNA[comuna]?.let { distanciaKm(desde, it) }
