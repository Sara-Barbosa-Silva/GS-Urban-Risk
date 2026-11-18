package com.fiap.ec.urbanrisk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fiap.ec.urbanrisk.model.TipoSensor;

class EstimadorRiscoPadraoTest {

    private final EstimadorRiscoPadrao estimador = new EstimadorRiscoPadrao();

    @Test
    void severidadeProporcionalAoLimite() {
        assertEquals(0.0, estimador.estimar(TipoSensor.PLUVIOMETRO, 20, 30, 3).severidade(), 0.001);
        assertEquals(0.5, estimador.estimar(TipoSensor.PLUVIOMETRO, 42, 30, 3).severidade(), 0.001);
        assertEquals(1.0, estimador.estimar(TipoSensor.PLUVIOMETRO, 90, 30, 3).severidade(), 0.001);
    }

    @Test
    void temperaturaUsaGrausAcimaDoLimite() {
        assertEquals(0.25, estimador.estimar(TipoSensor.TEMPERATURA, 35, 35, 27).severidade(), 0.001);
        assertEquals(1.0, estimador.estimar(TipoSensor.TEMPERATURA, 41, 35, 27).severidade(), 0.001);
        assertEquals(0.0, estimador.estimar(TipoSensor.TEMPERATURA, 30, 35, 27).severidade(), 0.001);
    }

    @Test
    void rioSensivelAPoucosCentimetros() {
        assertEquals(0.2, estimador.estimar(TipoSensor.NIVEL_RIO, 3.5, 3.5, 1.9).severidade(), 0.001);
        assertEquals(1.0, estimador.estimar(TipoSensor.NIVEL_RIO, 4.9, 3.5, 1.9).severidade(), 0.001);
    }

    @Test
    void descricaoEmLinguagemSimples() {
        String texto = estimador.estimar(TipoSensor.PLUVIOMETRO, 48, 30, 4).descricao();
        assertTrue(texto.startsWith("Chuva de 48,0 mm/h"));
        assertTrue(texto.contains("12,0x a média recente"));
    }
}
