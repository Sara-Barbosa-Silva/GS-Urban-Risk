package com.fiap.ec.urbanrisk.dto;

import java.time.Instant;

import com.fiap.ec.urbanrisk.model.TipoSensor;

public record LeituraDTO(
        Long id,
        String codigoSensor,
        TipoSensor tipoSensor,
        String regiaoNome,
        Double valor,
        String unidade,
        Instant dataHora,
        Double zScore,
        Boolean anomalia,
        Boolean acimaDoLimite,
        Long eventoId) {
}
