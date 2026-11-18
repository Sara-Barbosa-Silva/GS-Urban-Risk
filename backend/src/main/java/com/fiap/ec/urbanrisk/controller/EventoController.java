package com.fiap.ec.urbanrisk.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.ec.urbanrisk.dto.EventoDTO;
import com.fiap.ec.urbanrisk.dto.PaginaDTO;
import com.fiap.ec.urbanrisk.model.StatusEvento;
import com.fiap.ec.urbanrisk.model.TipoRisco;
import com.fiap.ec.urbanrisk.service.EventoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/eventos")
@Tag(name = "Eventos", description = "Histórico completo, inclusive resolvidos")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista todos os eventos, do mais recente para o mais antigo")
    public PaginaDTO<EventoDTO> listar(
            @RequestParam(required = false) StatusEvento status,
            @RequestParam(required = false) TipoRisco tipo,
            @RequestParam(required = false) Long regiaoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return service.listarEventos(status, tipo, regiaoId, page, size);
    }

    @PatchMapping("/{id}/resolver")
    @Operation(summary = "Agente local marca o evento como resolvido")
    public EventoDTO resolver(@PathVariable Long id) {
        return service.resolver(id);
    }
}
