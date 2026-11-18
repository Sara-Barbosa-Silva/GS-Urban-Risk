package com.fiap.ec.urbanrisk.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "leituras", indexes = {
        @Index(name = "idx_leitura_sensor_data", columnList = "sensor_id, dataHora")
})
public class Leitura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(nullable = false)
    private Double valor;

    @Column(nullable = false)
    private String unidade;

    @Column(nullable = false)
    private Instant dataHora;

    // Resultado da análise: z-score em relação às últimas leituras do sensor
    private Double zScore;

    @Column(nullable = false)
    private Boolean anomalia = false;

    public Leitura() {
    }

    public Leitura(Sensor sensor, Double valor, Instant dataHora) {
        this.sensor = sensor;
        this.valor = valor;
        this.unidade = sensor.getTipo().getUnidade();
        this.dataHora = dataHora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public Instant getDataHora() {
        return dataHora;
    }

    public void setDataHora(Instant dataHora) {
        this.dataHora = dataHora;
    }

    public Double getZScore() {
        return zScore;
    }

    public void setZScore(Double zScore) {
        this.zScore = zScore;
    }

    public Boolean getAnomalia() {
        return anomalia;
    }

    public void setAnomalia(Boolean anomalia) {
        this.anomalia = anomalia;
    }
}
