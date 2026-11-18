package com.fiap.ec.urbanrisk.model;

/**
 * Tipos de sensor e o risco que cada um ajuda a monitorar.
 */
public enum TipoSensor {
    PLUVIOMETRO("Pluviômetro", "mm/h", TipoRisco.ALAGAMENTO),
    NIVEL_RIO("Nível do rio", "m", TipoRisco.ALAGAMENTO),
    TEMPERATURA("Temperatura", "°C", TipoRisco.ILHA_DE_CALOR),
    REDE_ELETRICA("Rede elétrica", "% sem energia", TipoRisco.INTERRUPCAO_SERVICO);

    private final String descricao;
    private final String unidade;
    private final TipoRisco tipoRisco;

    TipoSensor(String descricao, String unidade, TipoRisco tipoRisco) {
        this.descricao = descricao;
        this.unidade = unidade;
        this.tipoRisco = tipoRisco;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUnidade() {
        return unidade;
    }

    public TipoRisco getTipoRisco() {
        return tipoRisco;
    }
}
