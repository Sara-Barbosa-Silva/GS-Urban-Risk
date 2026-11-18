package com.fiap.ec.urbanrisk.config;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fiap.ec.urbanrisk.dto.NovoRelatoDTO;
import com.fiap.ec.urbanrisk.model.Leitura;
import com.fiap.ec.urbanrisk.model.Regiao;
import com.fiap.ec.urbanrisk.model.Sensor;
import com.fiap.ec.urbanrisk.model.TipoRisco;
import com.fiap.ec.urbanrisk.model.TipoSensor;
import com.fiap.ec.urbanrisk.repository.LeituraRepository;
import com.fiap.ec.urbanrisk.repository.RegiaoRepository;
import com.fiap.ec.urbanrisk.repository.SensorRepository;
import com.fiap.ec.urbanrisk.service.BasePublicaService;
import com.fiap.ec.urbanrisk.service.RelatoService;

/**
 * Dados iniciais: 8 bairros de São Paulo, 12 sensores com histórico de leituras,
 * alertas da base pública (simulada) e dois relatos comunitários fictícios.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final RegiaoRepository regiaoRepository;
    private final SensorRepository sensorRepository;
    private final LeituraRepository leituraRepository;
    private final BasePublicaService basePublicaService;
    private final RelatoService relatoService;
    private final Clock clock;

    public DataLoader(RegiaoRepository regiaoRepository, SensorRepository sensorRepository,
            LeituraRepository leituraRepository, BasePublicaService basePublicaService,
            RelatoService relatoService, Clock clock) {
        this.regiaoRepository = regiaoRepository;
        this.sensorRepository = sensorRepository;
        this.leituraRepository = leituraRepository;
        this.basePublicaService = basePublicaService;
        this.relatoService = relatoService;
        this.clock = clock;
    }

    @Override
    public void run(String... args) {
        if (regiaoRepository.count() > 0) {
            log.info("Banco já possui dados. Importando atualizações da base pública.");
            basePublicaService.importar();
            return;
        }

        // Centro aproximado dos bairros e população estimada (valores aproximados, só para a demonstração)
        Regiao lapa = regiaoRepository.save(new Regiao("Lapa", "na", "Oeste", -23.5224, -46.7036, 305_000));
        Regiao mooca = regiaoRepository.save(new Regiao("Mooca", "na", "Leste", -23.5605, -46.5967, 345_000));
        Regiao itaim = regiaoRepository.save(new Regiao("Itaim Paulista", "no", "Leste", -23.5011, -46.3998, 375_000));
        Regiao pantanal = regiaoRepository.save(new Regiao("Jardim Pantanal", "no", "Leste", -23.4954, -46.4371, 90_000));
        Regiao butanta = regiaoRepository.save(new Regiao("Butantã", "no", "Oeste", -23.5713, -46.7086, 430_000));
        Regiao centro = regiaoRepository.save(new Regiao("Centro", "no", "Centro", -23.5489, -46.6388, 430_000));
        Regiao vilaPrudente = regiaoRepository.save(new Regiao("Vila Prudente", "na", "Leste", -23.5826, -46.5794, 245_000));
        Regiao santoAmaro = regiaoRepository.save(new Regiao("Santo Amaro", "em", "Sul", -23.6536, -46.7086, 240_000));

        List<Sensor> sensores = sensorRepository.saveAll(List.of(
                new Sensor("PLU-01", TipoSensor.PLUVIOMETRO, lapa, 30.0),
                new Sensor("PLU-02", TipoSensor.PLUVIOMETRO, mooca, 30.0),
                new Sensor("PLU-03", TipoSensor.PLUVIOMETRO, pantanal, 25.0),
                new Sensor("PLU-04", TipoSensor.PLUVIOMETRO, santoAmaro, 30.0),
                new Sensor("RIO-01", TipoSensor.NIVEL_RIO, pantanal, 3.5),
                new Sensor("RIO-02", TipoSensor.NIVEL_RIO, butanta, 4.0),
                new Sensor("TMP-01", TipoSensor.TEMPERATURA, centro, 35.0),
                new Sensor("TMP-02", TipoSensor.TEMPERATURA, mooca, 35.0),
                new Sensor("TMP-03", TipoSensor.TEMPERATURA, itaim, 35.0),
                new Sensor("ENE-01", TipoSensor.REDE_ELETRICA, santoAmaro, 10.0),
                new Sensor("ENE-02", TipoSensor.REDE_ELETRICA, itaim, 10.0),
                new Sensor("ENE-03", TipoSensor.REDE_ELETRICA, vilaPrudente, 10.0)));

        historico(sensores);
        basePublicaService.importar();

        // Relatos fictícios (sem dado pessoal)
        relatoService.criar(new NovoRelatoDTO(TipoRisco.ALAGAMENTO, pantanal.getId(),
                "Água na altura do joelho na rua perto da escola.", null, null, null, null));
        relatoService.criar(new NovoRelatoDTO(TipoRisco.ILHA_DE_CALOR, centro.getId(),
                "Praça sem sombra e bebedouro quebrado, muito calor.", null, null, null, null));

        log.info("Dados iniciais carregados: {} regiões, {} sensores.", regiaoRepository.count(), sensores.size());
    }

    // 20 leituras normais por sensor, para o z-score já ter base de comparação
    private void historico(List<Sensor> sensores) {
        Random random = new Random(42);
        Instant agora = Instant.now(clock);
        for (Sensor s : sensores) {
            for (int i = 20; i >= 1; i--) {
                double valor = switch (s.getTipo()) {
                    case PLUVIOMETRO -> Math.abs(3 + random.nextGaussian() * 1.5);
                    case NIVEL_RIO -> s.getLimiteAlerta() * 0.55 + random.nextGaussian() * s.getLimiteAlerta() * 0.02;
                    case TEMPERATURA -> 27 + random.nextGaussian();
                    case REDE_ELETRICA -> Math.abs(0.8 + random.nextGaussian() * 0.5);
                };
                Leitura l = new Leitura(s, Math.round(valor * 100) / 100.0, agora.minus(Duration.ofSeconds(10L * i)));
                l.setZScore(0.0);
                leituraRepository.save(l);
            }
        }
    }
}
