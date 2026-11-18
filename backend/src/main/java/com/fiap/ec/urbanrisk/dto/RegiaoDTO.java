package com.fiap.ec.urbanrisk.dto;

public record RegiaoDTO(
        Long id,
        String nome,
        String zona,
        Double latitude,
        Double longitude,
        Integer populacaoEstimada,
        long alertasAtivos) {
}
