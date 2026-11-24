package com.fundaciontextil.data.remote

import com.fundaciontextil.data.seguridad.Hasheador
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

// Responde las rutas de docs/API.md sin red, hasta que exista el servidor real.
class ServidorFalso(
    private val demora: Long = 300L,
    private val almacen: AlmacenCuentas = AlmacenCuentasEnMemoria(),
    private val dispatcherCalculo: CoroutineDispatcher = Dispatchers.Default,
) : ApiService {

    // Una petición a la vez, para que dos registros simultáneos no repitan el id.
    private val turno = Mutex()

    private val datosEmprendedoras = listOf(
        EmprendedoraDto(1, "María Soto", "Tejidos Soto", "Viña del Mar"),
        EmprendedoraDto(2, "Javiera Muñoz", "Lana Muñoz", "Quilpué"),
    )

    private val datosProductos = listOf(
        ProductoDto(1, 1, "Chal de lana merino", "Tejido a mano, 100% lana merino.", 18990, "chal-41.jpg"),
        ProductoDto(2, 1, "Bufanda de alpaca", "Tejido a telar.", 12500),
        ProductoDto(3, 2, "Gorro de lana reciclada", "Hecho con lana reciclada.", 6990),
    )

    // Crea las cuentas demo por adelantado: calcular sus hash es lento y no debe pasar en el primer registro.
    suspend fun preparar() {
        turno.withLock { withContext(dispatcherCalculo) { cuentas() } }
    }

    override suspend fun emprendedoras(): List<EmprendedoraDto> {
        delay(demora)
        return datosEmprendedoras
    }

    override suspend fun productosDe(emprendedoraId: Long): List<ProductoDto> {
        delay(demora)
        return datosProductos.filter { it.emprendedoraId == emprendedoraId }
    }

    override suspend fun registrar(datos: RegistroDto): UsuarioDto = atender {
        val cuentas = cuentas()
        val correo = normalizar(datos.correo)
        if (cuentas.any { it.usuario.correo == correo }) fallar(409, "CORREO_YA_REGISTRADO")
        val rutHash = Hasheador.hashRut(datos.rut)
        if (cuentas.any { it.rutHash == rutHash }) fallar(409, "RUT_YA_REGISTRADO")
        val sal = Hasheador.generarSal()
        val usuario = UsuarioDto(
            id = cuentas.maxOf { it.usuario.id } + 1,
            nombre = datos.nombre.trim(),
            correo = correo,
            rol = datos.rol,
            estado = if (datos.rol == "EMPRENDEDORA") "PENDIENTE" else "VALIDADA",
        )
        almacen.guardar(cuentas + CuentaServidor(usuario, rutHash, Hasheador.hashClave(datos.clave, sal), sal))
        usuario
    }

    override suspend fun iniciarSesion(datos: SesionDto): UsuarioDto = atender {
        val correo = normalizar(datos.correo)
        val cuenta = cuentas().find { it.usuario.correo == correo }
        if (cuenta == null || !Hasheador.claveCoincide(datos.clave, cuenta.sal, cuenta.claveHash)) {
            fallar(401, "CREDENCIALES_INCORRECTAS")
        }
        if (cuenta.usuario.estado == "DADA_DE_BAJA") fallar(403, "CUENTA_DADA_DE_BAJA")
        cuenta.usuario
    }

    override suspend fun usuario(id: Long): UsuarioDto = atender {
        cuentas().find { it.usuario.id == id }?.usuario ?: fallar(404, "NO_ENCONTRADO")
    }

    override suspend fun actualizarPerfil(id: Long, datos: PerfilDto): UsuarioDto = atender {
        val cuentas = cuentas()
        val actual = cuentas.find { it.usuario.id == id } ?: fallar(404, "NO_ENCONTRADO")
        val nuevo = actual.usuario.copy(
            nombre = datos.nombre,
            telefono = datos.telefono,
            region = datos.region,
            comuna = datos.comuna,
        )
        almacen.guardar(cuentas.map { if (it.usuario.id == id) it.copy(usuario = nuevo) else it })
        nuevo
    }

    // No envía ningún correo. Responde igual aunque la cuenta no exista, para no revelar quién está registrado.
    override suspend fun recuperarClave(datos: RecuperarClaveDto) {
        delay(demora)
    }

    private suspend fun <T> atender(respuesta: () -> T): T {
        delay(demora)
        return turno.withLock { withContext(dispatcherCalculo) { respuesta() } }
    }

    private fun cuentas(): List<CuentaServidor> =
        almacen.leer().ifEmpty { CuentasDeDemo.crear().also(almacen::guardar) }

    private fun normalizar(correo: String) = correo.trim().lowercase()

    // Lanza el mismo error que Retrofit, con el cuerpo { "codigo": ... } de docs/API.md.
    private fun fallar(codigoHttp: Int, codigo: String): Nothing {
        val cuerpo = """{"codigo":"$codigo"}""".toResponseBody("application/json".toMediaType())
        throw HttpException(Response.error<Any>(codigoHttp, cuerpo))
    }
}
