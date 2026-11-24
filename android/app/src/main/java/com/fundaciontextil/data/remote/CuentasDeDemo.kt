package com.fundaciontextil.data.remote

import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.seguridad.Hasheador

// Cuentas ficticias con las que parte el servidor falso.
object CuentasDeDemo {

    const val CLAVE_DEMO = "Textil2026"

    private class Demo(
        val nombre: String,
        val correo: String,
        val rut: String,
        val rol: Rol,
        val estado: EstadoCuenta,
    )

    private val demos = listOf(
        Demo("Camila Rojas", "camila@demo.cl", "11.111.111-1", Rol.CLIENTE, EstadoCuenta.VALIDADA),
        Demo("Marcela Soto", "marcela@demo.cl", "22.222.222-2", Rol.EMPRENDEDORA, EstadoCuenta.VALIDADA),
        Demo("Sandra Muñoz", "sandra@demo.cl", "33.333.333-3", Rol.EMPRENDEDORA, EstadoCuenta.PENDIENTE),
        Demo("Administración Demo", "admin@demo.cl", "44.444.444-4", Rol.ADMINISTRADORA, EstadoCuenta.VALIDADA),
    )

    fun crear(): List<CuentaServidor> = demos.mapIndexed { indice, demo ->
        val sal = Hasheador.generarSal()
        CuentaServidor(
            usuario = UsuarioDto(indice + 1L, demo.nombre, demo.correo, demo.rol.name, demo.estado.name),
            rutHash = Hasheador.hashRut(demo.rut),
            claveHash = Hasheador.hashClave(CLAVE_DEMO, sal),
            sal = sal,
        )
    }
}
