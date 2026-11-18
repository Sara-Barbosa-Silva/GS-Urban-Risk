package com.fiap.ec.urbanrisk.dto;

import java.time.Instant;
import java.util.List;

/**
 * Página de resultados. "geradoEm" é o horário do servidor no momento da consulta:
 * o app usa esse valor no próximo ?desde= para buscar só o que mudou.
 */
public record PaginaDTO<T>(
        List<T> conteudo,
        int pagina,
        int tamanho,
        long totalElementos,
        int totalPaginas,
        Instant geradoEm) {
}
