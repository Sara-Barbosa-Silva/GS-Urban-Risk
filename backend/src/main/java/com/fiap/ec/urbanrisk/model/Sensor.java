package com.fiap.ec.urbanrisk.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sensores")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSensor tipo;

    @ManyToOne
    @JoinColumn(name = "regiao_id", nullable = false)
    private Regiao regiao;

    @Column(nullable = false)
    private Boolean ativo = true;

    // Valor a partir do qual a leitura vira alerta (ex.: 30 mm/h de chuva)
    @Column(nullable = false)
    private Double limiteAlerta;

    public Sensor() {
    }

    public Sensor(String codigo, TipoSensor tipo, Regiao regiao, Double limiteAlerta) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.regiao = regiao;
        this.limiteAlerta = limiteAlerta;
        this.ativo = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoSensor getTipo() {
        return tipo;
    }

    public void setTipo(TipoSensor tipo) {
        this.tipo = tipo;
    }

    public Regiao getRegiao() {
        return regiao;
    }

    public void setRegiao(Regiao regiao) {
        this.regiao = regiao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Double getLimiteAlerta() {
        return limiteAlerta;
    }

    public void setLimiteAlerta(Double limiteAlerta) {
        this.limiteAlerta = limiteAlerta;
    }
}
