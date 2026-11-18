package com.fiap.ec.urbanrisk.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NovaLeituraDTO(
        @NotBlank(message = "Informe o código do sensor.")
        @Size(max = 30, message = "Código do sensor muito longo.")
        String codigoSensor,

        @NotNull(message = "Informe o valor da leitura.")
        Double valor,

        // Opcional: se não vier, usamos o horário do servidor
        Instant dataHora) {
}
