package com.fiap.ec.urbanrisk.dto;

import com.fiap.ec.urbanrisk.model.TipoSensor;

public record SensorDTO(
        Long id,
        String codigo,
        TipoSensor tipo,
        String tipoDescricao,
        Long regiaoId,
        String regiaoNome,
        Boolean ativo,
        Double limiteAlerta,
        String unidade,
        LeituraDTO ultimaLeitura) {
}
