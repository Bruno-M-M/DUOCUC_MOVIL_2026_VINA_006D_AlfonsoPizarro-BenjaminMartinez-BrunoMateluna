package com.fundaciontextil.data.remote

import kotlinx.coroutines.delay

class ServidorFalso(private val demora: Long = 300L) : ApiService {

    private val datosEmprendedoras = listOf(
        EmprendedoraDto(1, "María Soto", "Tejidos Soto", "Viña del Mar"),
        EmprendedoraDto(2, "Javiera Muñoz", "Lana Muñoz", "Quilpué"),
    )

    private val datosProductos = listOf(
        ProductoDto(1, 1, "Chal de lana merino", "Tejido a mano, 100% lana merino.", 18990, "chal-41.jpg"),
        ProductoDto(2, 1, "Bufanda de alpaca", "Tejido a telar.", 12500),
        ProductoDto(3, 2, "Gorro de lana reciclada", "Hecho con lana reciclada.", 6990),
    )

    override suspend fun emprendedoras(): List<EmprendedoraDto> {
        delay(demora)
        return datosEmprendedoras
    }

    override suspend fun productosDe(emprendedoraId: Long): List<ProductoDto> {
        delay(demora)
        return datosProductos.filter { it.emprendedoraId == emprendedoraId }
    }
}