package com.fundaciontextil.usuarios.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SaludController.class)
class SaludControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void salud_responde_ok() throws Exception {
        mvc.perform(get("/salud"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ok"))
                .andExpect(jsonPath("$.servicio").value("usuarios"));
    }
}
