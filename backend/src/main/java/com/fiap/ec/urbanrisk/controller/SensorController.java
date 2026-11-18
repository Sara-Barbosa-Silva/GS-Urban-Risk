package com.fiap.ec.urbanrisk.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fiap.ec.urbanrisk.dto.SensorDTO;
import com.fiap.ec.urbanrisk.service.SensorService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/sensores")
@Tag(name = "Sensores")
public class SensorController {

    private final SensorService service;

    public SensorController(SensorService service) {
        this.service = service;
    }

    @GetMapping
    public List<SensorDTO> listar(@RequestParam(required = false) Long regiaoId) {
        return service.listar(regiaoId);
    }
}
