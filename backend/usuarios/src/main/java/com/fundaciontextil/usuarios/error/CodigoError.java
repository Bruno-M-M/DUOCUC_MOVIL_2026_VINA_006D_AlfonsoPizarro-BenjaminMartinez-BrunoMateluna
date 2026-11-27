package com.fundaciontextil.usuarios.error;

import org.springframework.http.HttpStatus;

public enum CodigoError {
    DATOS_INVALIDOS(HttpStatus.BAD_REQUEST, "Los datos enviados no son válidos."),
    CREDENCIALES_INCORRECTAS(HttpStatus.UNAUTHORIZED, "Correo o clave incorrectos."),
    NO_AUTENTICADO(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión."),
    CUENTA_DADA_DE_BAJA(HttpStatus.FORBIDDEN, "La cuenta está dada de baja o suspendida."),
    PROHIBIDO(HttpStatus.FORBIDDEN, "No tienes permiso para esta acción."),
    NO_ENCONTRADO(HttpStatus.NOT_FOUND, "El recurso no existe."),
    CORREO_YA_REGISTRADO(HttpStatus.CONFLICT, "Ya existe una cuenta con ese correo."),
    RUT_YA_REGISTRADO(HttpStatus.CONFLICT, "Ya existe una cuenta con ese RUT."),
    YA_TIENE_FICHA(HttpStatus.CONFLICT, "Ya enviaste tu ficha de emprendedora."),
    ERROR_INTERNO(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error en el servidor.");

    private final HttpStatus estado;
    private final String mensaje;

    CodigoError(HttpStatus estado, String mensaje) {
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public HttpStatus estado() {
        return estado;
    }

    public String mensaje() {
        return mensaje;
    }
}
