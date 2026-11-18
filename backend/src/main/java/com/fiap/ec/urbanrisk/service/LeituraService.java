package com.fiap.ec.urbanrisk.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fiap.ec.urbanrisk.dto.LeituraDTO;
import com.fiap.ec.urbanrisk.dto.NovaLeituraDTO;
import com.fiap.ec.urbanrisk.exception.DadosInvalidosException;
import com.fiap.ec.urbanrisk.exception.RecursoNaoEncontradoException;
import com.fiap.ec.urbanrisk.model.Evento;
import com.fiap.ec.urbanrisk.model.Leitura;
import com.fiap.ec.urbanrisk.model.Sensor;
import com.fiap.ec.urbanrisk.repository.LeituraRepository;
import com.fiap.ec.urbanrisk.repository.SensorRepository;

/**
 * Recebe leituras de sensores, calcula o z-score e repassa para o EventoService.
 */
@Service
public class LeituraService {

    // Leituras mais antigas que isso são apagadas (o histórico longo iria para um data lake)
    static final Duration RETENCAO = Duration.ofHours(24);

    private final LeituraRepository leituraRepository;
    private final SensorRepository sensorRepository;
    private final PriorizacaoService priorizacao;
    private final EstimadorRisco estimador;
    private final EventoService eventoService;
    private final MapeadorDTO mapeador;
    private final Clock clock;

    public LeituraService(LeituraRepository leituraRepository, SensorRepository sensorRepository,
            PriorizacaoService priorizacao, EstimadorRisco estimador, EventoService eventoService,
            MapeadorDTO mapeador, Clock clock) {
        this.leituraRepository = leituraRepository;
        this.sensorRepository = sensorRepository;
        this.priorizacao = priorizacao;
        this.estimador = estimador;
        this.eventoService = eventoService;
        this.mapeador = mapeador;
        this.clock = clock;
    }

    @Transactional
    public LeituraDTO registrar(NovaLeituraDTO dto) {
        Sensor sensor = sensorRepository.findByCodigoIgnoreCase(dto.codigoSensor().trim())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sensor não encontrado: " + dto.codigoSensor()));
        if (!Boolean.TRUE.equals(sensor.getAtivo())) {
            throw new DadosInvalidosException("O sensor " + sensor.getCodigo() + " está desativado.");
        }
        if (dto.valor().isNaN() || dto.valor().isInfinite() || dto.valor() < 0) {
            throw new DadosInvalidosException("O valor da leitura deve ser um número maior ou igual a zero.");
        }
        Instant agora = Instant.now(clock);
        Instant dataHora = dto.dataHora() != null ? dto.dataHora() : agora;
        if (dataHora.isAfter(agora.plusSeconds(60))) {
            throw new DadosInvalidosException("A data da leitura não pode estar no futuro.");
        }

        // Z-score contra as últimas leituras do mesmo sensor (média móvel)
        List<Double> historico = leituraRepository.findTop20BySensorIdOrderByDataHoraDesc(sensor.getId())
                .stream().map(Leitura::getValor).toList();
        double z = priorizacao.zScore(historico, dto.valor());
        double media = priorizacao.media(historico);

        Leitura leitura = new Leitura(sensor, dto.valor(), dataHora);
        leitura.setZScore(z);
        leitura.setAnomalia(priorizacao.isAnomalia(z));
        leitura = leituraRepository.save(leitura);

        EstimadorRisco.Estimativa estimativa = estimador.estimar(sensor.getTipo(), dto.valor(),
                sensor.getLimiteAlerta(), media);
        Optional<Evento> evento = eventoService.processarLeitura(leitura, media, estimativa);
        return mapeador.leitura(leitura, evento.map(Evento::getId).orElse(null));
    }

    @Scheduled(fixedDelay = 3_600_000, initialDelay = 60_000)
    @Transactional
    public void limparAntigas() {
        leituraRepository.deleteByDataHoraBefore(Instant.now(clock).minus(RETENCAO));
    }
}
