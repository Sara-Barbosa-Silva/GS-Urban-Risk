package com.fiap.ec.urbanrisk.service;

import org.springframework.stereotype.Component;

import com.fiap.ec.urbanrisk.dto.EventoDTO;
import com.fiap.ec.urbanrisk.dto.LeituraDTO;
import com.fiap.ec.urbanrisk.dto.RegiaoDTO;
import com.fiap.ec.urbanrisk.dto.RelatoDTO;
import com.fiap.ec.urbanrisk.dto.SensorDTO;
import com.fiap.ec.urbanrisk.model.Evento;
import com.fiap.ec.urbanrisk.model.Leitura;
import com.fiap.ec.urbanrisk.model.Regiao;
import com.fiap.ec.urbanrisk.model.RelatoComunitario;
import com.fiap.ec.urbanrisk.model.Sensor;

/**
 * Converte entidades em DTOs. É aqui que a API decide o que pode ser exposto.
 */
@Component
public class MapeadorDTO {

    public EventoDTO evento(Evento e) {
        Regiao r = e.getRegiao();
        return new EventoDTO(
                e.getId(),
                e.getTipoRisco(),
                e.getTipoRisco().getDescricao(),
                r.getId(),
                r.getNome(),
                e.getTitulo(),
                e.getDescricao(),
                e.getOrigem(),
                e.getNivelConfianca(),
                fonte(e),
                e.getSeveridade(),
                e.getScorePrioridade(),
                e.getNivelAlerta(),
                e.getMotivoPrioridade(),
                e.getStatus(),
                e.getCriadoEm(),
                e.getAtualizadoEm(),
                e.getLatitude(),
                e.getLongitude(),
                e.getRelatoId(),
                e.getConfirmacoes(),
                e.getOrigensCorroborando());
    }

    /** Texto curto que diz de onde veio o dado. */
    public String fonte(Evento e) {
        String regiao = e.getRegiao().getNome();
        return switch (e.getOrigem()) {
            case SENSOR -> e.getNomeFonte() + " · " + regiao;
            case BASE_PUBLICA -> "Base pública " + e.getNomeFonte() + " (simulada) · " + e.getCodigoExterno();
            case COMUNITARIO -> {
                int c = e.getConfirmacoes() == null ? 0 : e.getConfirmacoes();
                String confirmacoes = c == 0 ? "ainda sem confirmações" : c == 1 ? "1 confirmação" : c + " confirmações";
                yield "Relato comunitário · " + confirmacoes;
            }
        };
    }

    public LeituraDTO leitura(Leitura l, Long eventoId) {
        Sensor s = l.getSensor();
        return new LeituraDTO(
                l.getId(),
                s.getCodigo(),
                s.getTipo(),
                s.getRegiao().getNome(),
                l.getValor(),
                l.getUnidade(),
                l.getDataHora(),
                l.getZScore() == null ? null : Math.round(l.getZScore() * 100) / 100.0,
                l.getAnomalia(),
                l.getValor() >= s.getLimiteAlerta(),
                eventoId);
    }

    public SensorDTO sensor(Sensor s, Leitura ultima) {
        return new SensorDTO(
                s.getId(),
                s.getCodigo(),
                s.getTipo(),
                s.getTipo().getDescricao(),
                s.getRegiao().getId(),
                s.getRegiao().getNome(),
                s.getAtivo(),
                s.getLimiteAlerta(),
                s.getTipo().getUnidade(),
                ultima == null ? null : leitura(ultima, null));
    }

    public RegiaoDTO regiao(Regiao r, long alertasAtivos) {
        return new RegiaoDTO(r.getId(), r.getNome(), r.getZona(), r.getLatitude(), r.getLongitude(),
                r.getPopulacaoEstimada(), alertasAtivos);
    }

    /** Relato público: sem contato e com a posição no centro do bairro. */
    public RelatoDTO relato(RelatoComunitario rel, Integer nivelConfianca) {
        Regiao r = rel.getRegiao();
        return new RelatoDTO(
                rel.getId(),
                rel.getTipoRisco(),
                r.getId(),
                r.getNome(),
                rel.getDescricao(),
                rel.getFotoUrl(),
                rel.getConfirmacoes(),
                rel.getContatoHash() != null,
                r.getLatitude(),
                r.getLongitude(),
                rel.getCriadoEm(),
                rel.getEventoId(),
                nivelConfianca);
    }
}
