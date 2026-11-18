package com.fiap.ec.urbanrisk.model;

/**
 * De onde veio o dado que gerou o evento.
 */
public enum Origem {
    SENSOR("Sensor"),
    BASE_PUBLICA("Base pública"),
    COMUNITARIO("Relato comunitário");

    private final String descricao;

    Origem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
