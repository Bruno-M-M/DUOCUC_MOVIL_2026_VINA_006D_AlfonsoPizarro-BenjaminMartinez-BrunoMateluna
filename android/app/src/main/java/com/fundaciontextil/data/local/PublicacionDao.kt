package com.fundaciontextil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PublicacionDao {
    @Query("SELECT * FROM emprendedoras ORDER BY nombre")
    suspend fun todasLasEmprendedoras(): List<EmprendedoraEntity>

    @Query("SELECT * FROM emprendedoras WHERE id = :id LIMIT 1")
    suspend fun emprendedoraPorId(id: Long): EmprendedoraEntity?

    @Query("SELECT * FROM emprendedoras WHERE usuarioId = :usuarioId LIMIT 1")
    suspend fun emprendedoraDeUsuario(usuarioId: Long): EmprendedoraEntity?

    @Query("SELECT * FROM emprendedoras WHERE usuarioId IS NULL AND nombre = :nombre LIMIT 1")
    suspend fun emprendedoraLibrePorNombre(nombre: String): EmprendedoraEntity?

    @Query("UPDATE emprendedoras SET usuarioId = :usuarioId WHERE id = :id")
    suspend fun vincular(id: Long, usuarioId: Long)

    @Insert
    suspend fun insertarEmprendedora(e: EmprendedoraEntity): Long

    @Insert
    suspend fun insertarProducto(p: ProductoEntity): Long

    @Query("SELECT fotoArchivo FROM productos WHERE fotoArchivo IS NOT NULL")
    suspend fun fotosDeProductosEnUso(): List<String>
}