package com.fundaciontextil.data.foto

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File
import java.io.IOException
import java.io.InputStream
import kotlin.math.max

object ProcesadorImagen {

    fun procesar(abrir: () -> InputStream, destino: File) {
        try {
            val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            abrir().use { BitmapFactory.decodeStream(it, null, medidas) }
            if (medidas.outWidth <= 0 || medidas.outHeight <= 0) throw IOException("No es una imagen válida")

            val opciones = BitmapFactory.Options().apply {
                inSampleSize = CalculoImagen.muestreo(medidas.outWidth, medidas.outHeight)
            }
            val original = abrir().use { BitmapFactory.decodeStream(it, null, opciones) }
                ?: throw IOException("No se pudo leer la imagen")

            val orientacion = abrir().use {
                ExifInterface(it).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL,
                )
            }

            val matriz = matrizDeOrientacion(orientacion)
            val factor = minOf(1f, CalculoImagen.LADO_MAXIMO.toFloat() / max(original.width, original.height))
            matriz.postScale(factor, factor)

            val ajustada = Bitmap.createBitmap(original, 0, 0, original.width, original.height, matriz, true)
            if (ajustada !== original) original.recycle()

            val listo = sobreFondoBlanco(ajustada)
            destino.outputStream().use { salida ->
                if (!listo.compress(Bitmap.CompressFormat.JPEG, CalculoImagen.CALIDAD_JPEG, salida)) {
                    throw IOException("No se pudo comprimir la imagen")
                }
            }
            listo.recycle()
        } catch (e: Exception) {
            destino.delete()
            throw e
        }
    }

    private fun sobreFondoBlanco(bitmap: Bitmap): Bitmap {
        if (!bitmap.hasAlpha()) return bitmap
        val salida = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        Canvas(salida).apply {
            drawColor(Color.WHITE)
            drawBitmap(bitmap, 0f, 0f, null)
        }
        bitmap.recycle()
        return salida
    }

    private fun matrizDeOrientacion(orientacion: Int): Matrix = Matrix().apply {
        when (orientacion) {
            ExifInterface.ORIENTATION_ROTATE_90 -> postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> postScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> { postRotate(90f); postScale(-1f, 1f) }
            ExifInterface.ORIENTATION_TRANSVERSE -> { postRotate(270f); postScale(-1f, 1f) }
        }
    }
}