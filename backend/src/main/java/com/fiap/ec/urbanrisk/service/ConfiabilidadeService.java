package com.fiap.ec.urbanrisk.service;

import org.springframework.stereotype.Service;

/**
 * Regras do nível de confiança (0 a 100) de cada origem de dado.
 * Java puro, sem acesso ao banco, para ser fácil de testar e explicar.
 */
@Service
public class ConfiabilidadeService {

    // Sensor
    public static final int BASE_SENSOR = 85;
    public static final int SENSOR_INATIVO = 30;
    public static final int PENALIDADE_SEM_LEITURA_RECENTE = 25;
    public static final long MINUTOS_LEITURA_RECENTE = 5;
    public static final double PROPORCAO_ANOMALIAS_TOLERADA = 0.3;
    public static final int PENALIDADE_MAXIMA_IRREGULAR = 20;

    // Base pública oficial
    public static final int BASE_PUBLICA = 90;

    // Relato comunitário
    public static final int BASE_RELATO = 40;
    public static final int BONUS_POR_CONFIRMACAO = 10;
    public static final int TETO_COM_CONFIRMACOES = 85;
    public static final int BONUS_SENSOR_CORROBORA = 15;

    /**
     * @param ativo                    sensor marcado como ativo
     * @param minutosDesdeUltimaLeitura minutos desde a última leitura recebida
     * @param proporcaoAnomalias       fração de leituras anômalas na última hora (0 a 1)
     */
    public int confiancaSensor(boolean ativo, long minutosDesdeUltimaLeitura, double proporcaoAnomalias) {
        if (!ativo) {
            return SENSOR_INATIVO;
        }
        int confianca = BASE_SENSOR;
        if (minutosDesdeUltimaLeitura > MINUTOS_LEITURA_RECENTE) {
            confianca -= PENALIDADE_SEM_LEITURA_RECENTE;
        }
        // Muitas leituras fora do padrão podem indicar defeito no sensor
        if (proporcaoAnomalias > PROPORCAO_ANOMALIAS_TOLERADA) {
            double excesso = (proporcaoAnomalias - PROPORCAO_ANOMALIAS_TOLERADA) / (1 - PROPORCAO_ANOMALIAS_TOLERADA);
            confianca -= (int) Math.round(excesso * PENALIDADE_MAXIMA_IRREGULAR);
        }
        return limitar(confianca);
    }

    public int confiancaBasePublica() {
        return BASE_PUBLICA;
    }

    /**
     * Relato: base 40, +10 por confirmação de outro morador (até 85),
     * +15 se um sensor na mesma região aponta o mesmo tipo de risco.
     */
    public int confiancaRelato(int confirmacoes, boolean sensorCorrobora) {
        int confianca = Math.min(TETO_COM_CONFIRMACOES, BASE_RELATO + BONUS_POR_CONFIRMACAO * Math.max(0, confirmacoes));
        if (sensorCorrobora) {
            confianca += BONUS_SENSOR_CORROBORA;
        }
        return limitar(confianca);
    }

    private static int limitar(int v) {
        return Math.max(0, Math.min(100, v));
    }
}
