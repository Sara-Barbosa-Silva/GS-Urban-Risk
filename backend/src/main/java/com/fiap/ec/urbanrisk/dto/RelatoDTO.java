package com.fiap.ec.urbanrisk.dto;

import java.time.Instant;

import com.fiap.ec.urbanrisk.model.TipoRisco;

/**
 * Relato como sai na API pública: sem contato e com a localização
 * no centro do bairro (não expõe onde a pessoa estava).
 */
public record RelatoDTO(
        Long id,
        TipoRisco tipoRisco,
        Long regiaoId,
        String regiaoNome,
        String descricao,
        String fotoUrl,
        Integer confirmacoes,
        Boolean contatoInformado,
        Double latitude,
        Double longitude,
        Instant criadoEm,
        Long eventoId,
        Integer nivelConfianca) {
}
