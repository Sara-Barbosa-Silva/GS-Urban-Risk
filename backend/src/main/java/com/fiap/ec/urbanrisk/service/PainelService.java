package com.fiap.ec.urbanrisk.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fiap.ec.urbanrisk.dto.LeituraDTO;
import com.fiap.ec.urbanrisk.dto.PainelDTO;
import com.fiap.ec.urbanrisk.model.Evento;
import com.fiap.ec.urbanrisk.model.Leitura;
import com.fiap.ec.urbanrisk.model.NivelAlerta;
import com.fiap.ec.urbanrisk.model.Regiao;
import com.fiap.ec.urbanrisk.model.StatusEvento;
import com.fiap.ec.urbanrisk.model.TipoRisco;
import com.fiap.ec.urbanrisk.repository.EventoRepository;
import com.fiap.ec.urbanrisk.repository.LeituraRepository;
import com.fiap.ec.urbanrisk.repository.SensorRepository;

@Service
public class PainelService {

    private final EventoRepository eventoRepository;
    private final LeituraRepository leituraRepository;
    private final SensorRepository sensorRepository;
    private final MapeadorDTO mapeador;
    private final Clock clock;
    private final boolean simuladorAtivo;

    public PainelService(EventoRepository eventoRepository, LeituraRepository leituraRepository,
            SensorRepository sensorRepository, MapeadorDTO mapeador, Clock clock,
            @Value("${app.simulador.enabled:false}") boolean simuladorAtivo) {
        this.eventoRepository = eventoRepository;
        this.leituraRepository = leituraRepository;
        this.sensorRepository = sensorRepository;
        this.mapeador = mapeador;
        this.clock = clock;
        this.simuladorAtivo = simuladorAtivo;
    }

    @Transactional(readOnly = true)
    public PainelDTO resumo() {
        List<Evento> ativos = eventoRepository.findByStatusOrderByScorePrioridadeDesc(StatusEvento.ATIVO);

        Map<String, Long> porNivel = new LinkedHashMap<>();
        for (NivelAlerta n : new NivelAlerta[] { NivelAlerta.CRITICO, NivelAlerta.ALTO, NivelAlerta.MODERADO,
                NivelAlerta.BAIXO }) {
            porNivel.put(n.name(), ativos.stream().filter(e -> e.getNivelAlerta() == n).count());
        }
        Map<String, Long> porTipo = new LinkedHashMap<>();
        for (TipoRisco t : TipoRisco.values()) {
            porTipo.put(t.name(), ativos.stream().filter(e -> e.getTipoRisco() == t).count());
        }

        List<LeituraDTO> leituras = sensorRepository.findByAtivoTrue().stream()
                .map(s -> leituraRepository.findTopBySensorIdOrderByDataHoraDesc(s.getId()).orElse(null))
                .filter(l -> l != null)
                .sorted(Comparator.comparing(Leitura::getDataHora).reversed())
                .map(l -> mapeador.leitura(l, null))
                .toList();

        Instant ultimaAtualizacao = ativos.stream().map(Evento::getAtualizadoEm).max(Comparator.naturalOrder())
                .orElse(null);
        Instant ultimaLeitura = leituraRepository.findTopByOrderByDataHoraDesc().map(Leitura::getDataHora).orElse(null);
        if (ultimaLeitura != null && (ultimaAtualizacao == null || ultimaLeitura.isAfter(ultimaAtualizacao))) {
            ultimaAtualizacao = ultimaLeitura;
        }

        return new PainelDTO(ativos.size(), porNivel, porTipo, regiaoMaisCritica(ativos), leituras,
                ultimaAtualizacao, Instant.now(clock), simuladorAtivo);
    }

    // Região com o alerta de maior score; empate decide pela quantidade de alertas
    private PainelDTO.RegiaoCritica regiaoMaisCritica(List<Evento> ativos) {
        if (ativos.isEmpty()) {
            return null;
        }
        Map<Regiao, List<Evento>> porRegiao = ativos.stream()
                .collect(Collectors.groupingBy(Evento::getRegiao));
        return porRegiao.entrySet().stream()
                .max(Comparator.<Map.Entry<Regiao, List<Evento>>>comparingDouble(
                        e -> e.getValue().stream().mapToDouble(Evento::getScorePrioridade).max().orElse(0))
                        .thenComparingInt(e -> e.getValue().size()))
                .map(e -> {
                    Evento pior = e.getValue().stream().max(Comparator.comparing(Evento::getScorePrioridade))
                            .orElseThrow();
                    return new PainelDTO.RegiaoCritica(e.getKey().getId(), e.getKey().getNome(), e.getValue().size(),
                            pior.getScorePrioridade(), pior.getNivelAlerta().name());
                })
                .orElse(null);
    }
}
