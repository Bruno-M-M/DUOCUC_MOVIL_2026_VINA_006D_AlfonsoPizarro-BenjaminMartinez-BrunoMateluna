package com.fundaciontextil.data.model

object ValidacionesProducto {
    const val PRECIO_MAXIMO = 10_000_000

    fun nombre(valor: String): String? = when {
        valor.trim().length < 3 -> "Escribe al menos 3 caracteres"
        valor.trim().length > 60 -> "Máximo 60 caracteres"
        else -> null
    }

    fun descripcion(valor: String): String? = when {
        valor.trim().length < 5 -> "Describe el producto (mínimo 5 caracteres)"
        valor.trim().length > 300 -> "Máximo 300 caracteres"
        else -> null
    }

    fun precio(valor: String): String? {
        val numero = valor.trim().toIntOrNull() ?: return "Ingresa un precio en pesos"
        return when {
            numero <= 0 -> "El precio debe ser mayor a 0"
            numero > PRECIO_MAXIMO -> "Precio demasiado alto"
            else -> null
        }
    }
}