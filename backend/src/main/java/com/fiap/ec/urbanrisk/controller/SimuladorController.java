package com.fiap.ec.urbanrisk.controller;

import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.ec.urbanrisk.exception.DadosInvalidosException;
import com.fiap.ec.urbanrisk.service.SimuladorSensoresService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/simulador")
@Tag(name = "Simulador", description = "Apoio à demonstração")
public class SimuladorController {

    private final ObjectProvider<SimuladorSensoresService> simulador;

    public SimuladorController(ObjectProvider<SimuladorSensoresService> simulador) {
        this.simulador = simulador;
    }

    @PostMapping("/pico/{codigoSensor}")
    @Operation(summary = "Força um pico de leitura num sensor (ex.: PLU-02) para gerar um alerta na hora")
    public Map<String, String> forcarPico(@PathVariable String codigoSensor) {
        SimuladorSensoresService s = simulador.getIfAvailable();
        if (s == null) {
            throw new DadosInvalidosException("O simulador está desligado (app.simulador.enabled=false).");
        }
        s.forcarPico(codigoSensor);
        return Map.of("mensagem", "Pico iniciado no sensor " + codigoSensor.toUpperCase()
                + ". O alerta aparece no app no próximo ciclo de atualização.");
    }
}
