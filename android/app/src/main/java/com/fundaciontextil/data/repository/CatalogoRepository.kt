package com.fundaciontextil.data.repository

import com.fundaciontextil.data.model.Emprendedora
import com.fundaciontextil.data.model.Producto
import kotlinx.coroutines.flow.Flow

interface CatalogoRepository {
    val emprendedoras: Flow<List<Emprendedora>>
    val productos: Flow<List<Producto>>
    fun productosDe(emprendedoraId:Long): Flow<List<Producto>>
}