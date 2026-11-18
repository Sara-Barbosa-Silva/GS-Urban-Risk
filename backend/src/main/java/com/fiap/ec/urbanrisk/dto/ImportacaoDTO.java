package com.fiap.ec.urbanrisk.dto;

public record ImportacaoDTO(
        String fonte,
        int lidos,
        int criados,
        int atualizados,
        int ignorados) {
}
