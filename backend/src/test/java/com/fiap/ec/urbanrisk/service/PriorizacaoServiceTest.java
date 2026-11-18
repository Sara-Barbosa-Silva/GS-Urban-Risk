package com.fiap.ec.urbanrisk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import com.fiap.ec.urbanrisk.model.NivelAlerta;

class PriorizacaoServiceTest {

    private final PriorizacaoService service = new PriorizacaoService();

    private PriorizacaoService.Fatores fatores(double severidade, int confianca, Duration idade, int origens) {
        return new PriorizacaoService.Fatores(severidade, confianca, 300_000, idade, origens, "Chuva de 40 mm/h");
    }

    @Test
    void scoreSobeComSeveridade() {
        double baixo = service.calcular(fatores(0.2, 85, Duration.ZERO, 1)).score();
        double alto = service.calcular(fatores(0.9, 85, Duration.ZERO, 1)).score();
        assertTrue(alto > baixo);
    }

    @Test
    void scoreSobeComConfianca() {
        double relato = service.calcular(fatores(0.5, 40, Duration.ZERO, 1)).score();
        double oficial = service.calcular(fatores(0.5, 90, Duration.ZERO, 1)).score();
        assertTrue(oficial > relato);
    }

    @Test
    void scoreDecaiComOTempo() {
        double agora = service.calcular(fatores(0.6, 85, Duration.ZERO, 1)).score();
        double tresHoras = service.calcular(fatores(0.6, 85, Duration.ofHours(3), 1)).score();
        double seisHoras = service.calcular(fatores(0.6, 85, Duration.ofHours(6), 1)).score();
        assertTrue(agora > tresHoras);
        assertTrue(tresHoras > seisHoras);
        assertEquals(0.5, service.recencia(Duration.ofHours(3)), 0.0001);
    }

    @Test
    void corroboracaoAumentaScore() {
        double sozinho = service.calcular(fatores(0.6, 85, Duration.ZERO, 1)).score();
        double duas = service.calcular(fatores(0.6, 85, Duration.ZERO, 2)).score();
        double tres = service.calcular(fatores(0.6, 85, Duration.ZERO, 3)).score();
        assertTrue(duas > sozinho);
        assertTrue(tres > duas);
    }

    @Test
    void scoreFicaEntreZeroECem() {
        assertEquals(100.0, service.calcular(new PriorizacaoService.Fatores(1, 100, 900_000, Duration.ZERO, 3, null)).score());
        assertEquals(0.0, service.calcular(new PriorizacaoService.Fatores(0, 0, 0, Duration.ofDays(30), 1, null)).score(), 0.1);
    }

    @Test
    void limitesDosNiveis() {
        assertEquals(NivelAlerta.CRITICO, service.nivel(75.0));
        assertEquals(NivelAlerta.ALTO, service.nivel(74.9));
        assertEquals(NivelAlerta.ALTO, service.nivel(55.0));
        assertEquals(NivelAlerta.MODERADO, service.nivel(54.9));
        assertEquals(NivelAlerta.MODERADO, service.nivel(35.0));
        assertEquals(NivelAlerta.BAIXO, service.nivel(34.9));
        assertEquals(NivelAlerta.BAIXO, service.nivel(0));
    }

    @Test
    void motivoExplicaAPrioridade() {
        String motivo = service.calcular(fatores(0.8, 85, Duration.ZERO, 2)).motivo();
        assertTrue(motivo.contains("Chuva de 40 mm/h"));
        assertTrue(motivo.contains("2 origens confirmam"));
        assertTrue(motivo.contains("confiança 85%"));
    }

    @Test
    void pesosSomamUm() {
        double soma = PriorizacaoService.PESO_SEVERIDADE + PriorizacaoService.PESO_CONFIANCA
                + PriorizacaoService.PESO_IMPACTO + PriorizacaoService.PESO_RECENCIA
                + PriorizacaoService.PESO_CORROBORACAO;
        assertEquals(1.0, soma, 0.0001);
    }
}
