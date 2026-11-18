package com.fiap.ec.urbanrisk.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Evento de risco: entidade central da plataforma.
 * Pode nascer de um sensor, de uma base pública ou de um relato comunitário,
 * e sempre carrega origem, nível de confiança e score de prioridade.
 */
@Entity
@Table(name = "eventos", indexes = {
        @Index(name = "idx_evento_status_score", columnList = "status, scorePrioridade"),
        @Index(name = "idx_evento_atualizado", columnList = "atualizadoEm"),
        @Index(name = "idx_evento_regiao_tipo", columnList = "regiao_id, tipoRisco"),
        @Index(name = "idx_evento_nivel", columnList = "nivelAlerta")
})
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRisco tipoRisco;

    @ManyToOne
    @JoinColumn(name = "regiao_id", nullable = false)
    private Regiao regiao;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 600)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Origem origem;

    // 0 a 100: quanto podemos confiar neste dado
    @Column(nullable = false)
    private Integer nivelConfianca;

    // 0 a 1: quão grave é a situação (ex.: quanto a leitura passou do limite)
    @Column(nullable = false)
    private Double severidade;

    // 0 a 100: resultado da priorização
    @Column(nullable = false)
    private Double scorePrioridade = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelAlerta nivelAlerta = NivelAlerta.BAIXO;

    @Column(length = 300)
    private String motivoPrioridade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEvento status = StatusEvento.ATIVO;

    @Column(nullable = false)
    private Instant criadoEm;

    @Column(nullable = false)
    private Instant atualizadoEm;

    private Double latitude;
    private Double longitude;

    // Referência à fonte: id do sensor, código do alerta da base pública ou id do relato
    private Long sensorId;
    private Long relatoId;
    private String codigoExterno;
    private String nomeFonte;

    // Quantas origens diferentes apontam o mesmo risco na mesma região (últimas 2h)
    @Column(nullable = false)
    private Integer origensCorroborando = 1;

    // Confirmações de moradores (só para relatos comunitários)
    @Column(nullable = false)
    private Integer confirmacoes = 0;

    // Momento da última evidência (leitura, confirmação, emissão). A recência do score usa este campo.
    @Column(nullable = false)
    private Instant ultimaEvidenciaEm;

    public Evento() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoRisco getTipoRisco() {
        return tipoRisco;
    }

    public void setTipoRisco(TipoRisco tipoRisco) {
        this.tipoRisco = tipoRisco;
    }

    public Regiao getRegiao() {
        return regiao;
    }

    public void setRegiao(Regiao regiao) {
        this.regiao = regiao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Origem getOrigem() {
        return origem;
    }

    public void setOrigem(Origem origem) {
        this.origem = origem;
    }

    public Integer getNivelConfianca() {
        return nivelConfianca;
    }

    public void setNivelConfianca(Integer nivelConfianca) {
        this.nivelConfianca = nivelConfianca;
    }

    public Double getSeveridade() {
        return severidade;
    }

    public void setSeveridade(Double severidade) {
        this.severidade = severidade;
    }

    public Double getScorePrioridade() {
        return scorePrioridade;
    }

    public void setScorePrioridade(Double scorePrioridade) {
        this.scorePrioridade = scorePrioridade;
    }

    public NivelAlerta getNivelAlerta() {
        return nivelAlerta;
    }

    public void setNivelAlerta(NivelAlerta nivelAlerta) {
        this.nivelAlerta = nivelAlerta;
    }

    public String getMotivoPrioridade() {
        return motivoPrioridade;
    }

    public void setMotivoPrioridade(String motivoPrioridade) {
        this.motivoPrioridade = motivoPrioridade;
    }

    public StatusEvento getStatus() {
        return status;
    }

    public void setStatus(StatusEvento status) {
        this.status = status;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Instant atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getSensorId() {
        return sensorId;
    }

    public void setSensorId(Long sensorId) {
        this.sensorId = sensorId;
    }

    public Long getRelatoId() {
        return relatoId;
    }

    public void setRelatoId(Long relatoId) {
        this.relatoId = relatoId;
    }

    public String getCodigoExterno() {
        return codigoExterno;
    }

    public void setCodigoExterno(String codigoExterno) {
        this.codigoExterno = codigoExterno;
    }

    public String getNomeFonte() {
        return nomeFonte;
    }

    public void setNomeFonte(String nomeFonte) {
        this.nomeFonte = nomeFonte;
    }

    public Integer getOrigensCorroborando() {
        return origensCorroborando;
    }

    public void setOrigensCorroborando(Integer origensCorroborando) {
        this.origensCorroborando = origensCorroborando;
    }

    public Integer getConfirmacoes() {
        return confirmacoes;
    }

    public void setConfirmacoes(Integer confirmacoes) {
        this.confirmacoes = confirmacoes;
    }

    public Instant getUltimaEvidenciaEm() {
        return ultimaEvidenciaEm;
    }

    public void setUltimaEvidenciaEm(Instant ultimaEvidenciaEm) {
        this.ultimaEvidenciaEm = ultimaEvidenciaEm;
    }
}
