package com.fundaciontextil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogoDao {
    @Query("SELECT * FROM emprendedoras ORDER BY nombre")
    fun observarEmprendedoras(): Flow<List<EmprendedoraEntity>>

    @Query("SELECT * FROM productos WHERE emprendedoraId = :id ORDER BY nombre")
    fun observarProductos(id: Long): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM productos ORDER BY nombre")
    fun observarTodosLosProductos(): Flow<List<ProductoEntity>>

    @Insert suspend fun insertarEmprendedora(e: EmprendedoraEntity): Long
    @Insert
    suspend fun insertarProducto(p: ProductoEntity): Long

    @Query("SELECT COUNT(*) FROM emprendedoras")
    suspend fun contarEmprendedoras(): Int
}