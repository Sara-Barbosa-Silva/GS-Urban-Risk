package com.fiap.ec.urbanrisk.model;

public enum TipoRisco {
    ALAGAMENTO("Alagamento"),
    ILHA_DE_CALOR("Ilha de calor"),
    INTERRUPCAO_SERVICO("Interrupção de serviço");

    private final String descricao;

    TipoRisco(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
