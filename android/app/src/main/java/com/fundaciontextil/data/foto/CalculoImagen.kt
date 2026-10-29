package com.fundaciontextil.data.foto

import kotlin.math.max
import kotlin.math.roundToInt

object CalculoImagen {
    const val LADO_MAXIMO = 1280
    const val CALIDAD_JPEG = 80

    fun tamanoFinal(ancho: Int, alto: Int, ladoMaximo: Int = LADO_MAXIMO): Pair<Int, Int> {
        val mayor = max(ancho, alto)
        if (mayor <= ladoMaximo) return ancho to alto
        val factor = ladoMaximo.toFloat() / mayor
        return max(1, (ancho * factor).roundToInt()) to max(1, (alto * factor).roundToInt())
    }

    fun muestreo(ancho: Int, alto: Int, ladoMaximo: Int = LADO_MAXIMO): Int {
        val mayor = max(ancho, alto)
        var muestreo = 1
        while (mayor / (muestreo * 2) >= ladoMaximo) muestreo *= 2
        return muestreo
    }
}