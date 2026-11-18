package com.fiap.ec.urbanrisk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.ec.urbanrisk.dto.LeituraDTO;
import com.fiap.ec.urbanrisk.dto.NovaLeituraDTO;
import com.fiap.ec.urbanrisk.service.LeituraService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/leituras")
@Tag(name = "Leituras", description = "Entrada de dados dos sensores")
public class LeituraController {

    private final LeituraService service;

    public LeituraController(LeituraService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Sensor envia uma leitura; acima do limite ou anômala vira alerta")
    public LeituraDTO registrar(@Valid @RequestBody NovaLeituraDTO dto) {
        return service.registrar(dto);
    }
}
