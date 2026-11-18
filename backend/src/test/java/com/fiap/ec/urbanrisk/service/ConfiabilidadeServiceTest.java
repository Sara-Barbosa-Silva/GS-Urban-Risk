package com.fiap.ec.urbanrisk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ConfiabilidadeServiceTest {

    private final ConfiabilidadeService service = new ConfiabilidadeService();

    @Test
    void sensorAtivoComLeituraRecenteTemBase85() {
        assertEquals(85, service.confiancaSensor(true, 0, 0.0));
    }

    @Test
    void sensorSemLeituraRecentePerdeConfianca() {
        assertEquals(60, service.confiancaSensor(true, 30, 0.0));
    }

    @Test
    void sensorComLeiturasIrregularesPerdeConfianca() {
        int irregular = service.confiancaSensor(true, 0, 1.0);
        assertEquals(65, irregular);
        assertTrue(service.confiancaSensor(true, 0, 0.6) < 85);
        assertEquals(85, service.confiancaSensor(true, 0, 0.3));
    }

    @Test
    void sensorInativoTemConfiancaBaixa() {
        assertEquals(ConfiabilidadeService.SENSOR_INATIVO, service.confiancaSensor(false, 0, 0.0));
    }

    @Test
    void basePublicaOficialTem90() {
        assertEquals(90, service.confiancaBasePublica());
    }

    @Test
    void relatoComecaEm40ESobe10PorConfirmacao() {
        assertEquals(40, service.confiancaRelato(0, false));
        assertEquals(50, service.confiancaRelato(1, false));
        assertEquals(80, service.confiancaRelato(4, false));
    }

    @Test
    void confirmacoesTemTetoDe85() {
        assertEquals(85, service.confiancaRelato(5, false));
        assertEquals(85, service.confiancaRelato(50, false));
    }

    @Test
    void sensorNaMesmaRegiaoSoma15() {
        assertEquals(55, service.confiancaRelato(0, true));
        assertEquals(100, service.confiancaRelato(10, true));
    }
}
