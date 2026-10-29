package com.fundaciontextil.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.Rol

@Entity(
    tableName = "usuarios",
    indices = [
        Index(value = ["correo"], unique = true),
        Index(value = ["rutHash"], unique = true),
    ],
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val rutHash: String,
    val claveHash: String,
    val sal: String,
    val rol: Rol,
    val estado: EstadoCuenta,
    val telefono: String? = null,
    val region: String? = null,
    val comuna: String? = null,
)