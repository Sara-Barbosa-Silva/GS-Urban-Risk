package com.fiap.ec.urbanrisk.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Bairro ou zona de São Paulo monitorado pela plataforma.
 */
@Entity
@Table(name = "regioes")
public class Regiao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private String zona;

    // "na", "no" ou "em": deixa os textos naturais ("Risco de alagamento na Mooca")
    @Column(nullable = false)
    private String preposicao = "em";

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    // População estimada: usada para medir o impacto de um evento
    @Column(nullable = false)
    private Integer populacaoEstimada;

    public Regiao() {
    }

    public Regiao(String nome, String preposicao, String zona, Double latitude, Double longitude,
            Integer populacaoEstimada) {
        this.nome = nome;
        this.preposicao = preposicao;
        this.zona = zona;
        this.latitude = latitude;
        this.longitude = longitude;
        this.populacaoEstimada = populacaoEstimada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPreposicao() {
        return preposicao;
    }

    public void setPreposicao(String preposicao) {
        this.preposicao = preposicao;
    }

    public String getNomeComPreposicao() {
        return preposicao + " " + nome;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
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

    public Integer getPopulacaoEstimada() {
        return populacaoEstimada;
    }

    public void setPopulacaoEstimada(Integer populacaoEstimada) {
        this.populacaoEstimada = populacaoEstimada;
    }
}
