package com.fiap.ec.urbanrisk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Detecção de anomalia por z-score sobre a média móvel das últimas leituras.
 */
class DeteccaoAnomaliaTest {

    private final PriorizacaoService service = new PriorizacaoService();

    private final List<Double> chuvaNormal = List.of(2.8, 3.1, 3.4, 2.5, 3.0, 2.9, 3.6, 3.2, 2.7, 3.3);

    @Test
    void serieNormalNaoDispara() {
        for (double valor : List.of(2.6, 3.0, 3.5, 3.7)) {
            double z = service.zScore(chuvaNormal, valor);
            assertFalse(service.isAnomalia(z), "valor " + valor + " não deveria ser anomalia (z=" + z + ")");
        }
    }

    @Test
    void picoDispara() {
        double z = service.zScore(chuvaNormal, 45.0);
        assertTrue(z > PriorizacaoService.Z_LIMITE);
        assertTrue(service.isAnomalia(z));
    }

    @Test
    void quedaBruscaTambemEAnomalia() {
        List<Double> temperatura = List.of(27.0, 27.4, 26.8, 27.1, 27.3, 26.9, 27.2);
        assertTrue(service.isAnomalia(service.zScore(temperatura, 20.0)));
    }

    @Test
    void poucasAmostrasNaoDisparam() {
        assertEquals(0.0, service.zScore(List.of(3.0, 3.1, 2.9), 50.0));
        assertEquals(0.0, service.zScore(List.of(), 50.0));
    }

    @Test
    void serieConstanteNaoGeraDivisaoPorZero() {
        double z = service.zScore(List.of(1.0, 1.0, 1.0, 1.0, 1.0, 1.0), 1.0);
        assertEquals(0.0, z, 0.0001);
        assertTrue(Double.isFinite(service.zScore(List.of(1.0, 1.0, 1.0, 1.0, 1.0), 5.0)));
    }
}
