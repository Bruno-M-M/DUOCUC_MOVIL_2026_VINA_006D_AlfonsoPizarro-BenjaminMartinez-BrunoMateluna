package com.fundaciontextil.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface FotoDao {
    @Query("SELECT archivo FROM fotos_perfil WHERE usuarioId = :usuarioId")
    fun observarArchivo(usuarioId: Long): Flow<String?>

    @Query("SELECT archivo FROM fotos_perfil WHERE usuarioId = :usuarioId")
    suspend fun archivoDe(usuarioId: Long): String?

    @Upsert
    suspend fun guardar(foto: FotoPerfilEntity)

    @Query("SELECT archivo FROM fotos_perfil")
    suspend fun todosLosArchivos(): List<String>
}