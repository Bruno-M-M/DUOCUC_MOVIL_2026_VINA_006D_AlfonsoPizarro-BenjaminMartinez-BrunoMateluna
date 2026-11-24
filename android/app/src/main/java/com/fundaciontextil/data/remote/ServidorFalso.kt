package com.fundaciontextil.data.remote

import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

class ServidorFalso(private val demora: Long = 300L) : ApiService {

    private data class CuentaFalsa(
        val usuario: UsuarioDto,
        val clave: String,
        val rut: String,
    )

    private val cuentas = mutableListOf(
        CuentaFalsa(
            usuario = UsuarioDto(1, "Camila Rojas", "camila@ejemplo.cl", "CLIENTE", "VALIDADA"),
            clave = "clave-segura-123",
            rut = "12.345.678-5",
        ),
    )
    private var siguienteId = 2L

    private val datosEmprendedoras = listOf(
        EmprendedoraDto(1, "María Soto", "Tejidos Soto", "Viña del Mar"),
        EmprendedoraDto(2, "Javiera Muñoz", "Lana Muñoz", "Quilpué"),
    )

    private val datosProductos = listOf(
        ProductoDto(1, 1, "Chal de lana merino", "Tejido a mano, 100% lana merino.", 18990, "chal-41.jpg"),
        ProductoDto(2, 1, "Bufanda de alpaca", "Tejido a telar.", 12500),
        ProductoDto(3, 2, "Gorro de lana reciclada", "Hecho con lana reciclada.", 6990),
    )

    override suspend fun emprendedoras(): List<EmprendedoraDto> {
        delay(demora)
        return datosEmprendedoras
    }

    override suspend fun productosDe(emprendedoraId: Long): List<ProductoDto> {
        delay(demora)
        return datosProductos.filter { it.emprendedoraId == emprendedoraId }
    }

    override suspend fun registrar(datos: RegistroDto): UsuarioDto {
        delay(demora)
        if (cuentas.any { it.usuario.correo == datos.correo }) fallar(409, "CORREO_YA_REGISTRADO")
        if (cuentas.any { it.rut == datos.rut }) fallar(409, "RUT_YA_REGISTRADO")
        val estado = if (datos.rol == "EMPRENDEDORA") "PENDIENTE" else "VALIDADA"
        val usuario = UsuarioDto(siguienteId++, datos.nombre, datos.correo, datos.rol, estado)
        cuentas.add(CuentaFalsa(usuario, datos.clave, datos.rut))
        return usuario
    }

    override suspend fun iniciarSesion(datos: SesionDto): UsuarioDto {
        delay(demora)
        val cuenta = cuentas.find { it.usuario.correo == datos.correo && it.clave == datos.clave }
            ?: fallar(401, "CREDENCIALES_INCORRECTAS")
        if (cuenta.usuario.estado == "DADA_DE_BAJA") fallar(403, "CUENTA_DADA_DE_BAJA")
        return cuenta.usuario
    }

    override suspend fun usuario(id: Long): UsuarioDto {
        delay(demora)
        return cuentas.find { it.usuario.id == id }?.usuario ?: fallar(404, "NO_ENCONTRADO")
    }

    override suspend fun actualizarPerfil(id: Long, datos: PerfilDto): UsuarioDto {
        delay(demora)
        val indice = cuentas.indexOfFirst { it.usuario.id == id }
        if (indice == -1) fallar(404, "NO_ENCONTRADO")
        val actual = cuentas[indice]
        val nuevo = actual.usuario.copy(
            nombre = datos.nombre,
            telefono = datos.telefono,
            region = datos.region,
            comuna = datos.comuna,
        )
        cuentas[indice] = actual.copy(usuario = nuevo)
        return nuevo
    }

    // Igual que el servidor real: responde 202 aunque el correo no exista.
    override suspend fun recuperarClave(datos: RecuperarClaveDto) {
        delay(demora)
    }

    private fun fallar(codigoHttp: Int, codigo: String): Nothing {
        val cuerpo = """{"codigo":"$codigo"}""".toResponseBody("application/json".toMediaType())
        throw HttpException(Response.error<Any>(codigoHttp, cuerpo))
    }
}