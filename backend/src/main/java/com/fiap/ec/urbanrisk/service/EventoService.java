package com.fiap.ec.urbanrisk.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fiap.ec.urbanrisk.dto.EventoDTO;
import com.fiap.ec.urbanrisk.dto.PaginaDTO;
import com.fiap.ec.urbanrisk.exception.DadosInvalidosException;
import com.fiap.ec.urbanrisk.exception.RecursoNaoEncontradoException;
import com.fiap.ec.urbanrisk.model.Evento;
import com.fiap.ec.urbanrisk.model.Leitura;
import com.fiap.ec.urbanrisk.model.NivelAlerta;
import com.fiap.ec.urbanrisk.model.Origem;
import com.fiap.ec.urbanrisk.model.Regiao;
import com.fiap.ec.urbanrisk.model.RelatoComunitario;
import com.fiap.ec.urbanrisk.model.Sensor;
import com.fiap.ec.urbanrisk.model.StatusEvento;
import com.fiap.ec.urbanrisk.model.TipoRisco;
import com.fiap.ec.urbanrisk.repository.EventoRepository;
import com.fiap.ec.urbanrisk.repository.LeituraRepository;

import jakarta.persistence.criteria.Predicate;

/**
 * Cria, atualiza e prioriza eventos a partir das três origens de dados.
 */
@Service
public class EventoService {

    private static final Logger log = LoggerFactory.getLogger(EventoService.class);

    // Leitura vira evento se passar do limite, ou se for anômala e estiver perto dele
    static final double RAZAO_MINIMA_ANOMALIA = 0.6;
    // Evento de sensor é encerrado quando a leitura volta abaixo de 80% do limite
    static final double RAZAO_NORMALIZACAO = 0.8;
    // Eventos sem nova evidência por 12 h são encerrados automaticamente
    static final Duration EXPIRACAO = Duration.ofHours(12);

    public static final int TAMANHO_MAXIMO_PAGINA = 200;

    private final EventoRepository eventoRepository;
    private final LeituraRepository leituraRepository;
    private final PriorizacaoService priorizacao;
    private final ConfiabilidadeService confiabilidade;
    private final MapeadorDTO mapeador;
    private final Clock clock;

    public EventoService(EventoRepository eventoRepository, LeituraRepository leituraRepository,
            PriorizacaoService priorizacao, ConfiabilidadeService confiabilidade, MapeadorDTO mapeador,
            Clock clock) {
        this.eventoRepository = eventoRepository;
        this.leituraRepository = leituraRepository;
        this.priorizacao = priorizacao;
        this.confiabilidade = confiabilidade;
        this.mapeador = mapeador;
        this.clock = clock;
    }

    // ---------- consultas ----------

    /**
     * Alertas ordenados por prioridade. Sem "desde": só ativos.
     * Com "desde": tudo que mudou depois desse horário, inclusive resolvidos,
     * para o app saber o que remover da tela.
     */
    @Transactional(readOnly = true)
    public PaginaDTO<EventoDTO> listarAlertas(TipoRisco tipo, Long regiaoId, NivelAlerta nivel, Instant desde,
            int pagina, int tamanho) {
        Instant agora = Instant.now(clock);
        StatusEvento status = desde == null ? StatusEvento.ATIVO : null;
        Sort ordem = Sort.by(Sort.Order.desc("scorePrioridade"), Sort.Order.desc("atualizadoEm"));
        return paginar(filtro(tipo, regiaoId, nivel, desde, status), pagina, tamanho, ordem, agora);
    }

    @Transactional(readOnly = true)
    public PaginaDTO<EventoDTO> listarEventos(StatusEvento status, TipoRisco tipo, Long regiaoId, int pagina,
            int tamanho) {
        Instant agora = Instant.now(clock);
        Sort ordem = Sort.by(Sort.Order.desc("atualizadoEm"));
        return paginar(filtro(tipo, regiaoId, null, null, status), pagina, tamanho, ordem, agora);
    }

    @Transactional(readOnly = true)
    public EventoDTO buscar(Long id) {
        return mapeador.evento(buscarEntidade(id));
    }

    public Evento buscarEntidade(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta não encontrado."));
    }

    // ---------- origem: sensor ----------

    /**
     * Decide o que fazer com uma leitura: abrir/atualizar o evento do sensor,
     * encerrar quando normalizou, ou não fazer nada.
     */
    @Transactional
    public Optional<Evento> processarLeitura(Leitura leitura, double mediaRecente, EstimadorRisco.Estimativa estimativa) {
        Sensor sensor = leitura.getSensor();
        Instant agora = Instant.now(clock);
        double razao = leitura.getValor() / sensor.getLimiteAlerta();
        boolean acimaDoLimite = razao >= 1.0;
        boolean anomaliaRelevante = Boolean.TRUE.equals(leitura.getAnomalia()) && razao >= RAZAO_MINIMA_ANOMALIA
                && leitura.getValor() > mediaRecente;
        Optional<Evento> existente = eventoRepository.findFirstBySensorIdAndStatus(sensor.getId(), StatusEvento.ATIVO);

        if (acimaDoLimite || anomaliaRelevante) {
            Evento evento = existente.orElseGet(() -> novoEvento(sensor.getTipo().getTipoRisco(), sensor.getRegiao(),
                    Origem.SENSOR, agora));
            evento.setSensorId(sensor.getId());
            evento.setNomeFonte("Sensor " + sensor.getCodigo());
            evento.setTitulo(tituloSensor(sensor));
            evento.setDescricao(sensor.getTipo().getDescricao() + " " + sensor.getCodigo() + ": "
                    + estimativa.descricao() + ".");
            evento.setSeveridade(Math.max(estimativa.severidade(), anomaliaRelevante ? 0.15 : 0));
            evento.setUltimaEvidenciaEm(agora);
            evento.setAtualizadoEm(agora);
            evento.setNivelConfianca(confiancaSensor(sensor, agora));
            repriorizar(evento, estimativa.descricao(), agora);
            Evento salvo = eventoRepository.save(evento);
            if (existente.isEmpty()) {
                log.info("Novo alerta de sensor {}: {} ({})", sensor.getCodigo(), salvo.getTitulo(), salvo.getNivelAlerta());
                repriorizarRelacionados(salvo, agora);
            }
            return Optional.of(salvo);
        }

        if (existente.isPresent() && razao < RAZAO_NORMALIZACAO) {
            Evento evento = existente.get();
            encerrar(evento, "Leituras voltaram ao normal", agora);
            eventoRepository.save(evento);
            repriorizarRelacionados(evento, agora);
            return Optional.of(evento);
        }
        return existente;
    }

    private String tituloSensor(Sensor sensor) {
        String onde = sensor.getRegiao().getNomeComPreposicao();
        return switch (sensor.getTipo()) {
            case PLUVIOMETRO -> "Chuva forte e risco de alagamento " + onde;
            case NIVEL_RIO -> "Rio subindo e risco de alagamento " + onde;
            case TEMPERATURA -> "Calor extremo " + onde;
            case REDE_ELETRICA -> "Falta de energia " + onde;
        };
    }

    private int confiancaSensor(Sensor sensor, Instant agora) {
        long minutos = leituraRepository.findTopBySensorIdOrderByDataHoraDesc(sensor.getId())
                .map(l -> Duration.between(l.getDataHora(), agora).toMinutes())
                .orElse(Long.MAX_VALUE);
        Instant umaHora = agora.minus(Duration.ofHours(1));
        long total = leituraRepository.countBySensorIdAndDataHoraAfter(sensor.getId(), umaHora);
        long anomalas = leituraRepository.countBySensorIdAndAnomaliaTrueAndDataHoraAfter(sensor.getId(), umaHora);
        double proporcao = total == 0 ? 0 : (double) anomalas / total;
        return confiabilidade.confiancaSensor(Boolean.TRUE.equals(sensor.getAtivo()), minutos, proporcao);
    }

    // ---------- origem: base pública ----------

    @Transactional
    public Evento registrarBasePublica(String codigo, String orgao, TipoRisco tipo, Regiao regiao, String titulo,
            String descricao, double severidade, Instant emitidoEm) {
        Instant agora = Instant.now(clock);
        Evento evento = eventoRepository.findFirstByCodigoExterno(codigo)
                .orElseGet(() -> novoEvento(tipo, regiao, Origem.BASE_PUBLICA, emitidoEm));
        evento.setCodigoExterno(codigo);
        evento.setNomeFonte(orgao);
        evento.setTitulo(titulo);
        evento.setDescricao(descricao);
        evento.setSeveridade(severidade);
        evento.setNivelConfianca(confiabilidade.confiancaBasePublica());
        if (evento.getUltimaEvidenciaEm() == null) {
            evento.setUltimaEvidenciaEm(emitidoEm);
        }
        evento.setAtualizadoEm(agora);
        repriorizar(evento, "Alerta oficial de " + orgao, agora);
        Evento salvo = eventoRepository.save(evento);
        repriorizarRelacionados(salvo, agora);
        return salvo;
    }

    public Optional<Evento> buscarPorCodigoExterno(String codigo) {
        return eventoRepository.findFirstByCodigoExterno(codigo);
    }

    // ---------- origem: relato comunitário ----------

    @Transactional
    public Evento registrarRelato(RelatoComunitario relato) {
        Instant agora = Instant.now(clock);
        Regiao regiao = relato.getRegiao();
        Evento evento = eventoRepository.findFirstByRelatoId(relato.getId())
                .orElseGet(() -> novoEvento(relato.getTipoRisco(), regiao, Origem.COMUNITARIO, relato.getCriadoEm()));
        evento.setRelatoId(relato.getId());
        evento.setNomeFonte("Relato comunitário");
        evento.setTitulo(tituloRelato(relato));
        evento.setDescricao(relato.getDescricao() == null || relato.getDescricao().isBlank()
                ? "Morador informou " + relato.getTipoRisco().getDescricao().toLowerCase() + " "
                        + regiao.getNomeComPreposicao() + "."
                : relato.getDescricao());
        evento.setConfirmacoes(relato.getConfirmacoes());
        // Mais confirmações indicam que a situação atinge mais gente
        evento.setSeveridade(Math.min(0.8, 0.45 + 0.05 * relato.getConfirmacoes()));
        evento.setUltimaEvidenciaEm(agora);
        evento.setAtualizadoEm(agora);
        repriorizar(evento, null, agora);
        Evento salvo = eventoRepository.save(evento);
        repriorizarRelacionados(salvo, agora);
        return salvo;
    }

    private String tituloRelato(RelatoComunitario relato) {
        String onde = relato.getRegiao().getNomeComPreposicao();
        return switch (relato.getTipoRisco()) {
            case ALAGAMENTO -> "Moradores relatam alagamento " + onde;
            case ILHA_DE_CALOR -> "Moradores relatam calor extremo " + onde;
            case INTERRUPCAO_SERVICO -> "Moradores relatam falta de serviço " + onde;
        };
    }

    // ---------- ações ----------

    @Transactional
    public EventoDTO resolver(Long id) {
        Evento evento = buscarEntidade(id);
        if (evento.getStatus() == StatusEvento.RESOLVIDO) {
            throw new DadosInvalidosException("Este alerta já está resolvido.");
        }
        Instant agora = Instant.now(clock);
        encerrar(evento, "Marcado como resolvido por agente local", agora);
        Evento salvo = eventoRepository.save(evento);
        repriorizarRelacionados(salvo, agora);
        return mapeador.evento(salvo);
    }

    /**
     * Recalcula o score de todos os ativos (a recência decai com o tempo)
     * e encerra eventos sem novidade há mais de 12 h.
     */
    @Scheduled(fixedDelayString = "${app.priorizacao.intervalo-ms:60000}", initialDelay = 30000)
    @Transactional
    public void repriorizarTodos() {
        Instant agora = Instant.now(clock);
        int alterados = 0;
        for (Evento evento : eventoRepository.findByStatusOrderByScorePrioridadeDesc(StatusEvento.ATIVO)) {
            if (evento.getUltimaEvidenciaEm().isBefore(agora.minus(EXPIRACAO))) {
                encerrar(evento, "Encerrado automaticamente: sem novas informações há 12 h", agora);
                eventoRepository.save(evento);
                alterados++;
                continue;
            }
            if (repriorizar(evento, null, agora)) {
                evento.setAtualizadoEm(agora);
                eventoRepository.save(evento);
                alterados++;
            }
        }
        if (alterados > 0) {
            log.debug("Repriorização: {} eventos alterados", alterados);
        }
    }

    // ---------- núcleo da priorização ----------

    /**
     * Recalcula corroboração, confiança (relatos) e score. Devolve true se o
     * nível mudou ou o score variou pelo menos 1 ponto.
     */
    boolean repriorizar(Evento evento, String descricaoSeveridade, Instant agora) {
        Regiao regiao = evento.getRegiao();
        List<Origem> origens = new ArrayList<>(eventoRepository.origensAtivas(regiao.getId(), evento.getTipoRisco(),
                agora.minus(PriorizacaoService.JANELA_CORROBORACAO)));
        if (evento.getStatus() == StatusEvento.ATIVO && !origens.contains(evento.getOrigem())) {
            origens.add(evento.getOrigem());
        }
        int corroborando = Math.max(1, origens.size());
        evento.setOrigensCorroborando(corroborando);

        if (evento.getOrigem() == Origem.COMUNITARIO) {
            boolean sensorCorrobora = origens.contains(Origem.SENSOR);
            evento.setNivelConfianca(confiabilidade.confiancaRelato(evento.getConfirmacoes(), sensorCorrobora));
        }

        String descricao = descricaoSeveridade != null ? descricaoSeveridade : descricaoAtual(evento);
        PriorizacaoService.Resultado r = priorizacao.calcular(new PriorizacaoService.Fatores(
                evento.getSeveridade(),
                evento.getNivelConfianca(),
                regiao.getPopulacaoEstimada(),
                Duration.between(evento.getUltimaEvidenciaEm(), agora),
                corroborando,
                descricao));

        boolean mudou = evento.getNivelAlerta() != r.nivel()
                || Math.abs(evento.getScorePrioridade() - r.score()) >= 1.0;
        evento.setScorePrioridade(r.score());
        evento.setNivelAlerta(r.nivel());
        evento.setMotivoPrioridade(r.motivo());
        return mudou;
    }

    // Reaproveita a primeira parte do motivo atual (o que foi medido) nas repriorizações
    private String descricaoAtual(Evento evento) {
        return switch (evento.getOrigem()) {
            case COMUNITARIO -> {
                int c = evento.getConfirmacoes();
                yield c == 0 ? "Relato de morador" : "Relato confirmado por " + c + (c == 1 ? " vizinho" : " vizinhos");
            }
            case BASE_PUBLICA -> "Alerta oficial de " + evento.getNomeFonte();
            case SENSOR -> {
                String motivo = evento.getMotivoPrioridade();
                yield motivo == null ? null : motivo.split(" \\+ ")[0];
            }
        };
    }

    // Outro evento no mesmo risco e região pode ganhar (ou perder) corroboração
    private void repriorizarRelacionados(Evento origem, Instant agora) {
        for (Evento outro : eventoRepository.findByRegiaoId(origem.getRegiao().getId())) {
            if (outro.getId().equals(origem.getId()) || outro.getStatus() != StatusEvento.ATIVO
                    || outro.getTipoRisco() != origem.getTipoRisco()) {
                continue;
            }
            if (repriorizar(outro, null, agora)) {
                outro.setAtualizadoEm(agora);
                eventoRepository.save(outro);
            }
        }
    }

    private void encerrar(Evento evento, String motivo, Instant agora) {
        evento.setStatus(StatusEvento.RESOLVIDO);
        evento.setMotivoPrioridade(motivo);
        evento.setAtualizadoEm(agora);
    }

    private Evento novoEvento(TipoRisco tipo, Regiao regiao, Origem origem, Instant criadoEm) {
        Evento e = new Evento();
        e.setTipoRisco(tipo);
        e.setRegiao(regiao);
        e.setOrigem(origem);
        e.setStatus(StatusEvento.ATIVO);
        e.setCriadoEm(criadoEm);
        e.setAtualizadoEm(criadoEm);
        e.setUltimaEvidenciaEm(criadoEm);
        e.setLatitude(regiao.getLatitude());
        e.setLongitude(regiao.getLongitude());
        e.setNivelConfianca(0);
        e.setSeveridade(0.0);
        return e;
    }

    private Specification<Evento> filtro(TipoRisco tipo, Long regiaoId, NivelAlerta nivel, Instant desde,
            StatusEvento status) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (status != null) {
                p.add(cb.equal(root.get("status"), status));
            }
            if (tipo != null) {
                p.add(cb.equal(root.get("tipoRisco"), tipo));
            }
            if (regiaoId != null) {
                p.add(cb.equal(root.get("regiao").get("id"), regiaoId));
            }
            if (nivel != null) {
                p.add(cb.equal(root.get("nivelAlerta"), nivel));
            }
            if (desde != null) {
                p.add(cb.greaterThan(root.get("atualizadoEm"), desde));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    private PaginaDTO<EventoDTO> paginar(Specification<Evento> filtro, int pagina, int tamanho, Sort ordem,
            Instant agora) {
        if (pagina < 0) {
            throw new DadosInvalidosException("A página deve ser 0 ou maior.");
        }
        if (tamanho < 1 || tamanho > TAMANHO_MAXIMO_PAGINA) {
            throw new DadosInvalidosException("O tamanho da página deve ficar entre 1 e " + TAMANHO_MAXIMO_PAGINA + ".");
        }
        Page<Evento> page = eventoRepository.findAll(filtro, PageRequest.of(pagina, tamanho, ordem));
        return new PaginaDTO<>(page.getContent().stream().map(mapeador::evento).toList(), page.getNumber(),
                page.getSize(), page.getTotalElements(), page.getTotalPages(), agora);
    }
}
