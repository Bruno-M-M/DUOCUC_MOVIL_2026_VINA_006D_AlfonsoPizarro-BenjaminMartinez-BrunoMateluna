package com.fundaciontextil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SolicitudEmprendedoraDao {
    @Insert
    suspend fun insertar(solicitud: SolicitudEmprendedoraEntity)

    @Query("SELECT * FROM solicitudes_emprendedora WHERE usuarioId = :usuarioId")
    suspend fun buscarPorUsuario(usuarioId: Long): SolicitudEmprendedoraEntity?
}