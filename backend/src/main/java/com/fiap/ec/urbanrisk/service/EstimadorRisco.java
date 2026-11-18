package com.fiap.ec.urbanrisk.service;

import com.fiap.ec.urbanrisk.model.TipoSensor;

/**
 * Estima a severidade (0 a 1) de uma leitura de sensor.
 *
 * A implementação padrão (EstimadorRiscoPadrao) é uma regra em Java puro.
 * Ponto de extensão: uma implementação alternativa pode chamar por HTTP o modelo de
 * regressão de risco de inundação treinado na disciplina de IA (ver README).
 */
public interface EstimadorRisco {

    Estimativa estimar(TipoSensor tipo, double valor, double limite, double mediaRecente);

    /**
     * @param severidade 0 (normal) a 1 (muito acima do limite)
     * @param descricao  texto curto e explicável, usado no motivo da prioridade
     */
    record Estimativa(double severidade, String descricao) {
    }
}
