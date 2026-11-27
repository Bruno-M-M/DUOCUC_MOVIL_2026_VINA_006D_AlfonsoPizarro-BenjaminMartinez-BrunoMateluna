package com.fundaciontextil.usuarios.error;

public record RespuestaError(String codigo, String mensaje) {

    public static RespuestaError de(CodigoError codigo) {
        return new RespuestaError(codigo.name(), codigo.mensaje());
    }
}
