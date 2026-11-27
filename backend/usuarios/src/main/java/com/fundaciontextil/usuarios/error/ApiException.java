package com.fundaciontextil.usuarios.error;

public class ApiException extends RuntimeException {

    private final CodigoError codigo;

    public ApiException(CodigoError codigo) {
        super(codigo.mensaje());
        this.codigo = codigo;
    }

    public CodigoError codigo() {
        return codigo;
    }
}
