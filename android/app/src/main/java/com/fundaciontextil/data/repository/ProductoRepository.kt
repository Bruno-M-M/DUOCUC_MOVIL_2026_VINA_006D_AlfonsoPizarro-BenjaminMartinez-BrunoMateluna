package com.fundaciontextil.data.repository

import com.fundaciontextil.data.model.Producto

object ProductoRepository {
    private val productos = listOf(
        Producto("1", "Poncho de Lana Artesanal", "Poncho tejido a mano con lana natural de oveja.", 35000, "Marcela Soto", "Tejidos"),
        Producto("2", "Bufanda Andina", "Bufanda suave con diseños tradicionales andinos.", 12000, "Marcela Soto", "Accesorios"),
        Producto("3", "Camino de Mesa Telar", "Camino de mesa elaborado en telar tradicional.", 18000, "Marcela Soto", "Hogar"),
        Producto("4", "Gorro de Alpaca", "Gorro abrigador de fibra de alpaca 100% natural.", 15000, "Marcela Soto", "Accesorios")
    )

    fun listarProductos(): List<Producto> = productos

    fun obtenerPorId(id: String): Producto? = productos.find { it.id == id }
}
