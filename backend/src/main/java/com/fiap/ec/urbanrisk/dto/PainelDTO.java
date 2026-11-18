package com.fiap.ec.urbanrisk.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Resumo para o painel de acompanhamento do app.
 */
public record PainelDTO(
        long totalAtivos,
        Map<String, Long> porNivel,
        Map<String, Long> porTipo,
        RegiaoCritica regiaoMaisCritica,
        List<LeituraDTO> ultimasLeituras,
        Instant ultimaAtualizacao,
        Instant geradoEm,
        boolean simuladorAtivo) {

    public record RegiaoCritica(Long id, String nome, long alertasAtivos, Double maiorScore, String nivel) {
    }
}
