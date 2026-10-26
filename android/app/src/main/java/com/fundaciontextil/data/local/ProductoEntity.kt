package com.fundaciontextil.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity("productos",
    foreignKeys = [ForeignKey(
        entity = EmprendedoraEntity :: class,
        parentColumns = ["id"],
        childColumns = ["emprendedoraId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("emprendedoraid")]

)
data class ProductoEntity (
    @PrimaryKey(autoGenerate = true) val id : Long = 0,
    val emprendedoraId: Long,
    val nombre: String,
    val descipcion: String,
    val precio : Int,
)