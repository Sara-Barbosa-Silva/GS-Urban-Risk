package com.fiap.ec.urbanrisk.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.ec.urbanrisk.dto.RegiaoDTO;
import com.fiap.ec.urbanrisk.service.RegiaoService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/regioes")
@Tag(name = "Regiões")
public class RegiaoController {

    private final RegiaoService service;

    public RegiaoController(RegiaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<RegiaoDTO> listar() {
        return service.listar();
    }
}
