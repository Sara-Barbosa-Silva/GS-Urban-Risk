package com.fiap.ec.urbanrisk.controller;

import java.time.Instant;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.ec.urbanrisk.dto.EventoDTO;
import com.fiap.ec.urbanrisk.dto.PaginaDTO;
import com.fiap.ec.urbanrisk.model.NivelAlerta;
import com.fiap.ec.urbanrisk.model.TipoRisco;
import com.fiap.ec.urbanrisk.service.EventoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/alertas")
@Tag(name = "Alertas", description = "Eventos ativos ordenados por prioridade")
public class AlertaController {

    private final EventoService service;

    public AlertaController(EventoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista alertas ativos, do mais prioritário para o menos",
            description = "Com ?desde= devolve tudo que mudou depois do horário informado, inclusive resolvidos.")
    public PaginaDTO<EventoDTO> listar(
            @RequestParam(required = false) TipoRisco tipo,
            @RequestParam(required = false) Long regiaoId,
            @RequestParam(required = false) NivelAlerta nivel,
            @Parameter(description = "ISO-8601, ex.: 2026-11-19T12:00:00Z")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant desde,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return service.listarAlertas(tipo, regiaoId, nivel, desde, page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhe de um alerta")
    public EventoDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }
}
