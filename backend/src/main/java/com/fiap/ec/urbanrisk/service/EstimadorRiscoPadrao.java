package com.fiap.ec.urbanrisk.service;

import java.util.Locale;

import org.springframework.stereotype.Component;

import com.fiap.ec.urbanrisk.model.TipoSensor;

/**
 * Severidade pela razão entre a leitura e o limite de alerta do sensor:
 * 0,8x do limite = 0 e 2x do limite (ou mais) = 1, de forma linear.
 */
@Component
public class EstimadorRiscoPadrao implements EstimadorRisco {

    static final double RAZAO_INICIO = 0.8;
    static final double RAZAO_MAXIMA = 2.0;
    private static final Locale PT_BR = Locale.of("pt", "BR");

    @Override
    public Estimativa estimar(TipoSensor tipo, double valor, double limite, double mediaRecente) {
        double razao = limite > 0 ? valor / limite : 0;
        double severidade = limitar((razao - RAZAO_INICIO) / (RAZAO_MAXIMA - RAZAO_INICIO));
        return new Estimativa(severidade, descrever(tipo, valor, limite, mediaRecente));
    }

    private String descrever(TipoSensor tipo, double valor, double limite, double mediaRecente) {
        String vezesMedia = "";
        if (mediaRecente > 0 && valor > mediaRecente * 1.5) {
            vezesMedia = String.format(PT_BR, " (%.1fx a média recente)", valor / mediaRecente);
        }
        return switch (tipo) {
            case PLUVIOMETRO -> String.format(PT_BR, "Chuva de %.1f mm/h, limite %.0f mm/h%s", valor, limite, vezesMedia);
            case NIVEL_RIO -> String.format(PT_BR, "Rio com %.2f m, limite %.2f m%s", valor, limite, vezesMedia);
            case TEMPERATURA -> String.format(PT_BR, "Temperatura de %.1f °C, limite %.0f °C", valor, limite);
            case REDE_ELETRICA -> String.format(PT_BR, "%.0f%% dos imóveis sem energia%s", valor, vezesMedia);
        };
    }

    private static double limitar(double v) {
        return Math.max(0, Math.min(1, v));
    }
}
