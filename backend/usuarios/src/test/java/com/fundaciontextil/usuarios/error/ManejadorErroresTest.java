package com.fundaciontextil.usuarios.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = ManejadorErroresTest.ControladorDePrueba.class)
@Import(ManejadorErroresTest.ControladorDePrueba.class)
class ManejadorErroresTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void una_ApiException_responde_con_su_estado_y_codigo() throws Exception {
        mvc.perform(get("/prueba/correo-repetido"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("CORREO_YA_REGISTRADO"))
                .andExpect(jsonPath("$.mensaje").value("Ya existe una cuenta con ese correo."));
    }

    @Test
    void un_campo_invalido_responde_DATOS_INVALIDOS() throws Exception {
        mvc.perform(post("/prueba/datos").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void un_json_mal_formado_responde_DATOS_INVALIDOS() throws Exception {
        mvc.perform(post("/prueba/datos").contentType(MediaType.APPLICATION_JSON).content("{nombre"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("DATOS_INVALIDOS"));
    }

    @Test
    void una_ruta_que_no_existe_responde_NO_ENCONTRADO() throws Exception {
        mvc.perform(get("/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("NO_ENCONTRADO"));
    }

    @Test
    void un_error_inesperado_responde_ERROR_INTERNO_sin_detalles() throws Exception {
        mvc.perform(get("/prueba/falla"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value("ERROR_INTERNO"))
                .andExpect(jsonPath("$.mensaje").value("Ocurrió un error en el servidor."));
    }

    record Datos(@NotBlank String nombre) {
    }

    @RestController
    static class ControladorDePrueba {

        @GetMapping("/prueba/correo-repetido")
        void correoRepetido() {
            throw new ApiException(CodigoError.CORREO_YA_REGISTRADO);
        }

        @PostMapping("/prueba/datos")
        void datos(@Valid @RequestBody Datos datos) {
        }

        @GetMapping("/prueba/falla")
        void falla() {
            throw new IllegalStateException("detalle interno");
        }
    }
}
