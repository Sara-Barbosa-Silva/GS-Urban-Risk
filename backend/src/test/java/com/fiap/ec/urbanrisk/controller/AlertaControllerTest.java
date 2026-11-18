package com.fiap.ec.urbanrisk.controller;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;

/**
 * Testa a API de ponta a ponta com o banco em memória e os dados do DataLoader.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AlertaControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void healthResponde() throws Exception {
        mvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    void alertasVemOrdenadosPorPrioridade() throws Exception {
        MvcResult res = mvc.perform(get("/api/alertas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo.length()", greaterThan(1)))
                .andExpect(jsonPath("$.geradoEm", notNullValue()))
                .andReturn();
        List<Double> scores = JsonPath.read(res.getResponse().getContentAsString(), "$.conteudo[*].scorePrioridade");
        for (int i = 1; i < scores.size(); i++) {
            assertTrue(scores.get(i - 1) >= scores.get(i), "lista fora de ordem: " + scores);
        }
    }

    @Test
    void todoAlertaInformaOrigemEConfianca() throws Exception {
        mvc.perform(get("/api/alertas"))
                .andExpect(jsonPath("$.conteudo[*]", everyItem(hasKey("origem"))))
                .andExpect(jsonPath("$.conteudo[*].nivelConfianca", everyItem(notNullValue())))
                .andExpect(jsonPath("$.conteudo[*].fonte", everyItem(notNullValue())))
                .andExpect(jsonPath("$.conteudo[*].motivoPrioridade", everyItem(notNullValue())));
    }

    @Test
    void filtroPorTipoFunciona() throws Exception {
        mvc.perform(get("/api/alertas").param("tipo", "ILHA_DE_CALOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo.length()", greaterThan(0)))
                .andExpect(jsonPath("$.conteudo[*].tipoRisco", everyItem(is("ILHA_DE_CALOR"))));
    }

    @Test
    void paginacaoRespeitaTamanho() throws Exception {
        mvc.perform(get("/api/alertas").param("size", "2"))
                .andExpect(jsonPath("$.conteudo.length()", is(2)))
                .andExpect(jsonPath("$.tamanho", is(2)));
    }

    @Test
    void desdeNoFuturoNaoTrazNada() throws Exception {
        mvc.perform(get("/api/alertas").param("desde", "2999-01-01T00:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conteudo.length()", is(0)));
    }

    @Test
    void idInexistenteDevolve404() throws Exception {
        mvc.perform(get("/api/alertas/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro", is("Alerta não encontrado.")));
    }

    @Test
    void filtroInvalidoDevolve400() throws Exception {
        mvc.perform(get("/api/alertas").param("tipo", "TERREMOTO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro", notNullValue()));
    }

    @Test
    void relatoInvalidoDevolve400ComMensagem() throws Exception {
        mvc.perform(post("/api/relatos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipoRisco\":\"ALAGAMENTO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro", is("Escolha a região.")));
    }

    @Test
    void relatoComRegiaoInexistenteDevolve404() throws Exception {
        mvc.perform(post("/api/relatos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipoRisco\":\"ALAGAMENTO\",\"regiaoId\":9999}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro", is("Região não encontrada.")));
    }

    @Test
    void relatoNaoDevolveContatoEConfirmacaoAumentaConfianca() throws Exception {
        MvcResult criado = mvc.perform(post("/api/relatos").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"tipoRisco":"ALAGAMENTO","regiaoId":2,"descricao":"Rua alagada",
                         "contato":"11988887777","latitude":-23.561234,"longitude":-46.598765}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contatoInformado", is(true)))
                .andExpect(jsonPath("$", not(hasKey("contato"))))
                .andExpect(jsonPath("$", not(hasKey("contatoHash"))))
                .andExpect(jsonPath("$.nivelConfianca", is(40)))
                .andReturn();
        String corpo = criado.getResponse().getContentAsString();
        assertTrue(!corpo.contains("11988887777"));
        Integer id = JsonPath.read(corpo, "$.id");

        mvc.perform(post("/api/relatos/" + id + "/confirmar").header("X-Dispositivo-Id", "aparelho-A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivelConfianca", is(50)))
                .andExpect(jsonPath("$.confirmacoes", is(1)))
                .andExpect(jsonPath("$.origem", is("COMUNITARIO")));

        // O mesmo aparelho não confirma duas vezes
        mvc.perform(post("/api/relatos/" + id + "/confirmar").header("X-Dispositivo-Id", "aparelho-A"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void leituraAcimaDoLimiteGeraAlertaDeSensor() throws Exception {
        mvc.perform(post("/api/leituras").contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigoSensor\":\"PLU-01\",\"valor\":55.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.acimaDoLimite", is(true)))
                .andExpect(jsonPath("$.anomalia", is(true)))
                .andExpect(jsonPath("$.eventoId", notNullValue()));

        mvc.perform(get("/api/alertas").param("regiaoId", "1").param("tipo", "ALAGAMENTO"))
                .andExpect(jsonPath("$.conteudo[?(@.origem == 'SENSOR')].fonte", everyItem(is("Sensor PLU-01 · Lapa"))));
    }

    @Test
    void leituraDeSensorInexistenteDevolve404() throws Exception {
        mvc.perform(post("/api/leituras").contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigoSensor\":\"XXX-99\",\"valor\":1.0}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void agenteResolveAlerta() throws Exception {
        MvcResult res = mvc.perform(get("/api/alertas").param("tipo", "INTERRUPCAO_SERVICO")).andReturn();
        Integer id = JsonPath.read(res.getResponse().getContentAsString(), "$.conteudo[0].id");

        mvc.perform(patch("/api/eventos/" + id + "/resolver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RESOLVIDO")));

        mvc.perform(patch("/api/eventos/" + id + "/resolver"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void painelTrazResumo() throws Exception {
        mvc.perform(get("/api/painel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porNivel.CRITICO", notNullValue()))
                .andExpect(jsonPath("$.porTipo.ALAGAMENTO", notNullValue()))
                .andExpect(jsonPath("$.regiaoMaisCritica.nome", notNullValue()))
                .andExpect(jsonPath("$.ultimasLeituras.length()", greaterThan(0)));
    }

    @Test
    void regioesESensoresListados() throws Exception {
        mvc.perform(get("/api/regioes")).andExpect(jsonPath("$.length()", is(8)));
        mvc.perform(get("/api/sensores")).andExpect(jsonPath("$.length()", is(12)));
    }
}
