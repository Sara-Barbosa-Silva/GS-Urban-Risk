package com.fiap.ec.urbanrisk.dto;

import java.time.Instant;

import com.fiap.ec.urbanrisk.model.NivelAlerta;
import com.fiap.ec.urbanrisk.model.Origem;
import com.fiap.ec.urbanrisk.model.StatusEvento;
import com.fiap.ec.urbanrisk.model.TipoRisco;

/**
 * Evento/alerta como sai na API. Sempre informa origem, confiança e fonte,
 * e nunca carrega dado pessoal de quem relatou.
 */
public record EventoDTO(
        Long id,
        TipoRisco tipoRisco,
        String tipoRiscoDescricao,
        Long regiaoId,
        String regiaoNome,
        String titulo,
        String descricao,
        Origem origem,
        Integer nivelConfianca,
        String fonte,
        Double severidade,
        Double scorePrioridade,
        NivelAlerta nivelAlerta,
        String motivoPrioridade,
        StatusEvento status,
        Instant criadoEm,
        Instant atualizadoEm,
        Double latitude,
        Double longitude,
        Long relatoId,
        Integer confirmacoes,
        Integer origensCorroborando) {
}
