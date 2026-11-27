package com.fundaciontextil.usuarios.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ManejadorErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<RespuestaError> alFallarLaApi(ApiException e) {
        return responder(e.codigo());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<RespuestaError> alRecibirDatosInvalidos(Exception e) {
        return responder(CodigoError.DATOS_INVALIDOS);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RespuestaError> alNoEncontrarLaRuta(NoResourceFoundException e) {
        return responder(CodigoError.NO_ENCONTRADO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> alFallarInesperadamente(Exception e) {
        log.error("Error no controlado", e);
        return responder(CodigoError.ERROR_INTERNO);
    }

    private ResponseEntity<RespuestaError> responder(CodigoError codigo) {
        return ResponseEntity.status(codigo.estado()).body(RespuestaError.de(codigo));
    }
}
