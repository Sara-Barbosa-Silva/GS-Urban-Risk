package com.fiap.ec.urbanrisk.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.ec.urbanrisk.dto.PainelDTO;
import com.fiap.ec.urbanrisk.service.PainelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/painel")
@Tag(name = "Painel", description = "Resumo para o painel de acompanhamento")
public class PainelController {

    private final PainelService service;

    public PainelController(PainelService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Totais por nível e tipo, região mais crítica e últimas leituras")
    public PainelDTO resumo() {
        return service.resumo();
    }
}
