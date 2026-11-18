package com.fiap.ec.urbanrisk.service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fiap.ec.urbanrisk.dto.NovaLeituraDTO;
import com.fiap.ec.urbanrisk.exception.RecursoNaoEncontradoException;
import com.fiap.ec.urbanrisk.model.Sensor;
import com.fiap.ec.urbanrisk.repository.SensorRepository;

/**
 * Simula sensores reais: a cada 10 s cada sensor ativo envia uma leitura plausível
 * (chuva, nível do rio, temperatura, rede elétrica). De vez em quando começa um
 * "episódio" de pico, que dura de 40 a 80 s e vira alerta.
 * Ligado por app.simulador.enabled=true.
 */
@Service
@ConditionalOnProperty(name = "app.simulador.enabled", havingValue = "true")
public class SimuladorSensoresService {

    private static final Logger log = LoggerFactory.getLogger(SimuladorSensoresService.class);

    private final SensorRepository sensorRepository;
    private final LeituraService leituraService;
    private final double chancePico;
    private final Random random = new Random();

    // Episódios de pico em andamento: código do sensor -> leituras restantes e intensidade
    private final Map<String, Pico> picos = new ConcurrentHashMap<>();

    record Pico(int restantes, double multiplicador) {
    }

    public SimuladorSensoresService(SensorRepository sensorRepository, LeituraService leituraService,
            @Value("${app.simulador.chance-pico:0.03}") double chancePico) {
        this.sensorRepository = sensorRepository;
        this.leituraService = leituraService;
        this.chancePico = chancePico;
    }

    @Scheduled(fixedDelayString = "${app.simulador.intervalo-ms:10000}", initialDelay = 5000)
    public void gerarLeituras() {
        for (Sensor sensor : sensorRepository.findByAtivoTrue()) {
            try {
                leituraService.registrar(new NovaLeituraDTO(sensor.getCodigo(), gerarValor(sensor), null));
            } catch (RuntimeException e) {
                log.warn("Simulador: falha ao registrar leitura de {}: {}", sensor.getCodigo(), e.getMessage());
            }
        }
    }

    /** Força um pico num sensor (usado na demonstração). */
    public void forcarPico(String codigo) {
        Sensor sensor = sensorRepository.findByCodigoIgnoreCase(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sensor não encontrado: " + codigo));
        picos.put(sensor.getCodigo(), new Pico(6, 1.6 + random.nextDouble() * 0.5));
        leituraService.registrar(new NovaLeituraDTO(sensor.getCodigo(), gerarValor(sensor), null));
    }

    double gerarValor(Sensor sensor) {
        double limite = sensor.getLimiteAlerta();
        Pico pico = picos.get(sensor.getCodigo());
        if (pico == null && random.nextDouble() < chancePico) {
            pico = new Pico(4 + random.nextInt(5), 1.15 + random.nextDouble() * 0.9);
        }
        if (pico != null) {
            if (pico.restantes() <= 1) {
                picos.remove(sensor.getCodigo());
            } else {
                picos.put(sensor.getCodigo(), new Pico(pico.restantes() - 1, pico.multiplicador()));
            }
            return arredondar(limite * pico.multiplicador() * (0.92 + random.nextDouble() * 0.16));
        }
        return arredondar(Math.max(0, normal(sensor, limite)));
    }

    // Valores do dia a dia, abaixo do limite
    private double normal(Sensor sensor, double limite) {
        return switch (sensor.getTipo()) {
            case PLUVIOMETRO -> Math.abs(3 + random.nextGaussian() * 1.5);
            case NIVEL_RIO -> limite * 0.55 + random.nextGaussian() * limite * 0.02;
            case TEMPERATURA -> 27 + random.nextGaussian() * 1.0;
            case REDE_ELETRICA -> Math.abs(0.8 + random.nextGaussian() * 0.5);
        };
    }

    private static double arredondar(double v) {
        return Math.round(v * 100) / 100.0;
    }
}
