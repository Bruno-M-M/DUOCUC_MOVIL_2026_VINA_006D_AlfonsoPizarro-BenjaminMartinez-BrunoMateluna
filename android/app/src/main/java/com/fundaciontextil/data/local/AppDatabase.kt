package com.fundaciontextil.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UsuarioEntity::class,
        EmprendedoraEntity::class,
        ProductoEntity::class,
        SolicitudEmprendedoraEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun catalogoDao(): CatalogoDao

    abstract fun solicitudEmprendedoraDao(): SolicitudEmprendedoraDao

    companion object {
        fun crear(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "fundacion_textil.db")
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}