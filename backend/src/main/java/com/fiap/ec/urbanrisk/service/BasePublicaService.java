package com.fiap.ec.urbanrisk.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fiap.ec.urbanrisk.dto.ImportacaoDTO;
import com.fiap.ec.urbanrisk.exception.DadosInvalidosException;
import com.fiap.ec.urbanrisk.model.Evento;
import com.fiap.ec.urbanrisk.model.Regiao;
import com.fiap.ec.urbanrisk.model.StatusEvento;
import com.fiap.ec.urbanrisk.model.TipoRisco;
import com.fiap.ec.urbanrisk.repository.RegiaoRepository;

import tools.jackson.databind.ObjectMapper;

/**
 * Importa alertas de base pública. Para a apresentação não depender de internet,
 * lê um JSON no formato de órgãos como Defesa Civil, CGE e INMET (dados simulados).
 * A importação é idempotente: o mesmo código de alerta nunca vira dois eventos.
 */
@Service
public class BasePublicaService {

    private static final Logger log = LoggerFactory.getLogger(BasePublicaService.class);
    static final String ARQUIVO = "dados-publicos/alertas-defesa-civil.json";

    private final RegiaoRepository regiaoRepository;
    private final EventoService eventoService;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public BasePublicaService(RegiaoRepository regiaoRepository, EventoService eventoService,
            ObjectMapper objectMapper, Clock clock) {
        this.regiaoRepository = regiaoRepository;
        this.eventoService = eventoService;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    record ArquivoAlertas(String fonte, String fonteReal, List<AlertaPublico> alertas) {
    }

    record AlertaPublico(String codigo, String orgao, TipoRisco tipo, String bairro, String severidade,
            String titulo, String descricao, Integer emitidoHaMinutos) {
    }

    @Transactional
    public ImportacaoDTO importar() {
        ArquivoAlertas arquivo = ler();
        Instant agora = Instant.now(clock);
        int criados = 0;
        int atualizados = 0;
        int ignorados = 0;

        for (AlertaPublico a : arquivo.alertas()) {
            Optional<Regiao> regiao = regiaoRepository.findByNomeIgnoreCase(a.bairro());
            if (regiao.isEmpty() || a.codigo() == null || a.tipo() == null) {
                ignorados++;
                continue;
            }
            Optional<Evento> existente = eventoService.buscarPorCodigoExterno(a.codigo());
            if (existente.isPresent() && existente.get().getStatus() == StatusEvento.RESOLVIDO) {
                ignorados++;
                continue;
            }
            int minutos = a.emitidoHaMinutos() == null ? 0 : a.emitidoHaMinutos();
            eventoService.registrarBasePublica(a.codigo(), a.orgao(), a.tipo(), regiao.get(), a.titulo(),
                    a.descricao(), severidade(a.severidade()), agora.minus(Duration.ofMinutes(minutos)));
            if (existente.isPresent()) {
                atualizados++;
            } else {
                criados++;
            }
        }
        log.info("Base pública importada: {} criados, {} atualizados, {} ignorados", criados, atualizados, ignorados);
        return new ImportacaoDTO(arquivo.fonte(), arquivo.alertas().size(), criados, atualizados, ignorados);
    }

    /** Severidade do órgão convertida para a escala 0 a 1 da plataforma. */
    static double severidade(String nivel) {
        if (nivel == null) {
            return 0.5;
        }
        return switch (nivel.toUpperCase()) {
            case "EXTREMA" -> 1.0;
            case "ALTA" -> 0.85;
            case "MODERADA" -> 0.6;
            case "BAIXA" -> 0.3;
            default -> 0.5;
        };
    }

    private ArquivoAlertas ler() {
        try (InputStream in = new ClassPathResource(ARQUIVO).getInputStream()) {
            return objectMapper.readValue(in, ArquivoAlertas.class);
        } catch (IOException e) {
            throw new DadosInvalidosException("Não foi possível ler a base pública simulada.");
        }
    }
}
