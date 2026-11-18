package com.fiap.ec.urbanrisk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI urbanRiskOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Urban Risk Intelligence Platform - API")
                .version("1.0")
                .description("Monitoramento de alagamentos, ilhas de calor e interrupções de serviços. "
                        + "Recebe dados de sensores, bases públicas e relatos comunitários e devolve "
                        + "alertas priorizados, com origem e nível de confiança."));
    }
}
