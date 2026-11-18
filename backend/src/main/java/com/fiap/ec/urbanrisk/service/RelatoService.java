package com.fiap.ec.urbanrisk.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fiap.ec.urbanrisk.dto.EventoDTO;
import com.fiap.ec.urbanrisk.dto.NovoRelatoDTO;
import com.fiap.ec.urbanrisk.dto.RelatoDTO;
import com.fiap.ec.urbanrisk.exception.DadosInvalidosException;
import com.fiap.ec.urbanrisk.exception.RecursoNaoEncontradoException;
import com.fiap.ec.urbanrisk.model.ConfirmacaoRelato;
import com.fiap.ec.urbanrisk.model.Evento;
import com.fiap.ec.urbanrisk.model.Regiao;
import com.fiap.ec.urbanrisk.model.RelatoComunitario;
import com.fiap.ec.urbanrisk.model.StatusEvento;
import com.fiap.ec.urbanrisk.repository.ConfirmacaoRelatoRepository;
import com.fiap.ec.urbanrisk.repository.RegiaoRepository;
import com.fiap.ec.urbanrisk.repository.RelatoComunitarioRepository;

/**
 * Relatos de moradores: criação, confirmação por vizinhos e retenção de dados (LGPD).
 */
@Service
public class RelatoService {

    private final RelatoComunitarioRepository relatoRepository;
    private final ConfirmacaoRelatoRepository confirmacaoRepository;
    private final RegiaoRepository regiaoRepository;
    private final EventoService eventoService;
    private final LgpdService lgpd;
    private final MapeadorDTO mapeador;
    private final Clock clock;
    private final int diasRetencaoContato;

    public RelatoService(RelatoComunitarioRepository relatoRepository,
            ConfirmacaoRelatoRepository confirmacaoRepository, RegiaoRepository regiaoRepository,
            EventoService eventoService, LgpdService lgpd, MapeadorDTO mapeador, Clock clock,
            @Value("${app.lgpd.dias-retencao-contato:30}") int diasRetencaoContato) {
        this.relatoRepository = relatoRepository;
        this.confirmacaoRepository = confirmacaoRepository;
        this.regiaoRepository = regiaoRepository;
        this.eventoService = eventoService;
        this.lgpd = lgpd;
        this.mapeador = mapeador;
        this.clock = clock;
        this.diasRetencaoContato = diasRetencaoContato;
    }

    @Transactional
    public RelatoDTO criar(NovoRelatoDTO dto) {
        Regiao regiao = regiaoRepository.findById(dto.regiaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Região não encontrada."));

        RelatoComunitario relato = new RelatoComunitario();
        relato.setTipoRisco(dto.tipoRisco());
        relato.setRegiao(regiao);
        relato.setDescricao(limpar(dto.descricao()));
        relato.setFotoUrl(limpar(dto.fotoUrl()));
        relato.setContatoHash(lgpd.hash(dto.contato()));
        relato.setLatitudeAproximada(lgpd.arredondar(dto.latitude()));
        relato.setLongitudeAproximada(lgpd.arredondar(dto.longitude()));
        relato.setCriadoEm(Instant.now(clock));
        relato = relatoRepository.save(relato);

        Evento evento = eventoService.registrarRelato(relato);
        relato.setEventoId(evento.getId());
        relato = relatoRepository.save(relato);
        return mapeador.relato(relato, evento.getNivelConfianca());
    }

    /**
     * Outro morador confirma o relato: a confiança sobe 10 pontos por confirmação.
     * Se o app enviar o id do aparelho, o mesmo aparelho só confirma uma vez.
     */
    @Transactional
    public EventoDTO confirmar(Long relatoId, String dispositivoId) {
        RelatoComunitario relato = relatoRepository.findById(relatoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relato não encontrado."));
        Evento evento = eventoService.buscarEntidade(relato.getEventoId());
        if (evento.getStatus() == StatusEvento.RESOLVIDO) {
            throw new DadosInvalidosException("Este relato já foi resolvido e não aceita mais confirmações.");
        }

        String dispositivoHash = lgpd.hash(dispositivoId);
        if (dispositivoHash != null) {
            if (confirmacaoRepository.existsByRelatoIdAndDispositivoHash(relatoId, dispositivoHash)) {
                throw new DadosInvalidosException("Você já confirmou este relato. Obrigado!");
            }
            confirmacaoRepository.save(new ConfirmacaoRelato(relatoId, dispositivoHash, Instant.now(clock)));
        }

        relato.setConfirmacoes(relato.getConfirmacoes() + 1);
        relatoRepository.save(relato);
        return mapeador.evento(eventoService.registrarRelato(relato));
    }

    @Transactional(readOnly = true)
    public List<RelatoDTO> listar() {
        return relatoRepository.findAllByOrderByCriadoEmDesc().stream()
                .map(r -> mapeador.relato(r, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public RelatoDTO buscar(Long id) {
        RelatoComunitario relato = relatoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relato não encontrado."));
        Integer confianca = relato.getEventoId() == null ? null
                : eventoService.buscarEntidade(relato.getEventoId()).getNivelConfianca();
        return mapeador.relato(relato, confianca);
    }

    /** Retenção LGPD: depois de 30 dias o hash do contato é apagado. */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public int anonimizarAntigos() {
        Instant limite = Instant.now(clock).minus(Duration.ofDays(diasRetencaoContato));
        List<RelatoComunitario> antigos = relatoRepository.findByContatoHashIsNotNullAndCriadoEmBefore(limite);
        antigos.forEach(r -> r.setContatoHash(null));
        relatoRepository.saveAll(antigos);
        return antigos.size();
    }

    private static String limpar(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return texto.trim();
    }
}
