package com.fiap.ec.urbanrisk.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.ec.urbanrisk.dto.ImportacaoDTO;
import com.fiap.ec.urbanrisk.service.BasePublicaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/importacoes")
@Tag(name = "Importações", description = "Entrada de dados de bases públicas")
public class ImportacaoController {

    private final BasePublicaService service;

    public ImportacaoController(BasePublicaService service) {
        this.service = service;
    }

    @PostMapping("/base-publica")
    @Operation(summary = "Importa os alertas da base pública (simulada, sem internet)")
    public ImportacaoDTO importarBasePublica() {
        return service.importar();
    }
}
