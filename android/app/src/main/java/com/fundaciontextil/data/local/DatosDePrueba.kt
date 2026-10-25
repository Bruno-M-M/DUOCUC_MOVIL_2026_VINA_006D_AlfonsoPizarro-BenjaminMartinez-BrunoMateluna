package com.fundaciontextil.data.local

import com.fundaciontextil.data.model.EstadoCuenta
import com.fundaciontextil.data.model.Rol
import com.fundaciontextil.data.seguridad.Hasheador

class DatosDePrueba(private val dao: UsuarioDao) {

    private class CuentaDemo(
        val nombre: String,
        val correo: String,
        val rut: String,
        val rol: Rol,
        val estado: EstadoCuenta,
    )

    private val cuentas = listOf(
        CuentaDemo("Camila Rojas", "camila@demo.cl", "11.111.111-1", Rol.CLIENTE, EstadoCuenta.VALIDADA),
        CuentaDemo("Marcela Soto", "marcela@demo.cl", "22.222.222-2", Rol.EMPRENDEDORA, EstadoCuenta.VALIDADA),
        CuentaDemo("Sandra Muñoz", "sandra@demo.cl", "33.333.333-3", Rol.EMPRENDEDORA, EstadoCuenta.PENDIENTE),
        CuentaDemo("Administración Demo", "admin@demo.cl", "44.444.444-4", Rol.ADMINISTRADORA, EstadoCuenta.VALIDADA),
    )

    suspend fun cargarSiHaceFalta() {
        for (cuenta in cuentas) {
            if (dao.buscarPorCorreo(cuenta.correo) != null) continue
            val sal = Hasheador.generarSal()
            dao.insertar(
                UsuarioEntity(
                    nombre = cuenta.nombre,
                    correo = cuenta.correo,
                    rutHash = Hasheador.hashRut(cuenta.rut),
                    claveHash = Hasheador.hashClave(CLAVE_DEMO, sal),
                    sal = sal,
                    rol = cuenta.rol,
                    estado = cuenta.estado,
                ),
            )
        }
    }

    companion object {
        const val CLAVE_DEMO = "Textil2026"
    }
}