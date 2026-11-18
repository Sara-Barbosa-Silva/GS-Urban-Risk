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
 * Relato enviado por um morador pelo app.
 * LGPD: não guardamos nome. O contato (opcional) fica só como hash SHA-256 com salt
 * e nunca é devolvido pela API. A localização é guardada com precisão reduzida.
 */
@Entity
@Table(name = "relatos", indexes = {
        @Index(name = "idx_relato_regiao", columnList = "regiao_id")
})
public class RelatoComunitario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRisco tipoRisco;

    @ManyToOne
    @JoinColumn(name = "regiao_id", nullable = false)
    private Regiao regiao;

    @Column(length = 500)
    private String descricao;

    @Column(length = 300)
    private String fotoUrl;

    @Column(nullable = false)
    private Integer confirmacoes = 0;

    // Hash do telefone/e-mail para retorno. Nunca o valor original.
    @Column(length = 64)
    private String contatoHash;

    // Coordenadas arredondadas (2 casas, cerca de 1 km)
    private Double latitudeAproximada;
    private Double longitudeAproximada;

    @Column(nullable = false)
    private Instant criadoEm;

    private Long eventoId;

    public RelatoComunitario() {
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public Integer getConfirmacoes() {
        return confirmacoes;
    }

    public void setConfirmacoes(Integer confirmacoes) {
        this.confirmacoes = confirmacoes;
    }

    public String getContatoHash() {
        return contatoHash;
    }

    public void setContatoHash(String contatoHash) {
        this.contatoHash = contatoHash;
    }

    public Double getLatitudeAproximada() {
        return latitudeAproximada;
    }

    public void setLatitudeAproximada(Double latitudeAproximada) {
        this.latitudeAproximada = latitudeAproximada;
    }

    public Double getLongitudeAproximada() {
        return longitudeAproximada;
    }

    public void setLongitudeAproximada(Double longitudeAproximada) {
        this.longitudeAproximada = longitudeAproximada;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }
}
