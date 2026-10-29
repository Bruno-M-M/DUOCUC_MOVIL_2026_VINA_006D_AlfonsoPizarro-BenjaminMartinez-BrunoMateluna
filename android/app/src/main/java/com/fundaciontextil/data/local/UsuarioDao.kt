package com.fundaciontextil.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE rutHash = :rutHash LIMIT 1")
    suspend fun buscarPorRutHash(rutHash: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Long): UsuarioEntity?

    @Query("UPDATE usuarios SET nombre = :nombre, telefono = :telefono, region = :region, comuna = :comuna WHERE id = :id")
    suspend fun actualizarPerfil(id: Long, nombre: String, telefono: String?, region: String?, comuna: String?)
}