package com.fiap.ec.urbanrisk.dto;

import com.fiap.ec.urbanrisk.model.TipoRisco;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Relato enviado pelo morador. Não pede nome. O contato é opcional
 * e só serve para retorno da Defesa Civil (guardado como hash).
 */
public record NovoRelatoDTO(
        @NotNull(message = "Escolha o tipo de risco.")
        TipoRisco tipoRisco,

        @NotNull(message = "Escolha a região.")
        Long regiaoId,

        @Size(max = 500, message = "A descrição pode ter no máximo 500 caracteres.")
        String descricao,

        @Size(max = 300, message = "O endereço da foto é muito longo.")
        @Pattern(regexp = "^$|^https?://.+", message = "A foto deve ser um link http ou https.")
        String fotoUrl,

        @Size(max = 120, message = "O contato pode ter no máximo 120 caracteres.")
        String contato,

        @DecimalMin(value = "-90.0", message = "Latitude inválida.")
        @DecimalMax(value = "90.0", message = "Latitude inválida.")
        Double latitude,

        @DecimalMin(value = "-180.0", message = "Longitude inválida.")
        @DecimalMax(value = "180.0", message = "Longitude inválida.")
        Double longitude) {
}
