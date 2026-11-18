package com.fiap.ec.urbanrisk.model;

/**
 * Nível do alerta, derivado do score de prioridade (0 a 100).
 * Os limites ficam em PriorizacaoService e são explicados no README.
 */
public enum NivelAlerta {
    BAIXO,
    MODERADO,
    ALTO,
    CRITICO
}
