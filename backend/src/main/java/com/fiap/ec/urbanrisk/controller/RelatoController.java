package com.fiap.ec.urbanrisk.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.ec.urbanrisk.dto.EventoDTO;
import com.fiap.ec.urbanrisk.dto.NovoRelatoDTO;
import com.fiap.ec.urbanrisk.dto.RelatoDTO;
import com.fiap.ec.urbanrisk.service.RelatoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/relatos")
@Tag(name = "Relatos comunitários", description = "Moradores informam e confirmam riscos")
public class RelatoController {

    private final RelatoService service;

    public RelatoController(RelatoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Morador envia um relato (nome não é pedido; contato opcional vira hash)")
    public RelatoDTO criar(@Valid @RequestBody NovoRelatoDTO dto) {
        return service.criar(dto);
    }

    @PostMapping("/{id}/confirmar")
    @Operation(summary = "Outro morador confirma o relato e a confiança sobe")
    public EventoDTO confirmar(@PathVariable Long id,
            @RequestHeader(name = "X-Dispositivo-Id", required = false) String dispositivoId) {
        return service.confirmar(id, dispositivoId);
    }

    @GetMapping
    @Operation(summary = "Lista relatos (sem dados pessoais)")
    public List<RelatoDTO> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public RelatoDTO buscar(@PathVariable Long id) {
        return service.buscar(id);
    }
}
