/**
 * ConexaoContext - dados em tempo real compartilhados por todas as telas
 *
 * Faz polling a cada 5 s (alertas que mudaram + painel), guarda o último
 * resultado em cache, detecta queda do servidor e reconecta sozinho com backoff.
 */
import React, { createContext, useCallback, useEffect, useMemo, useRef, useState } from "react";

import { Alerta } from "../interfaces/alerta";
import { Painel } from "../interfaces/painel";
import { StatusConexao } from "../types/statusConexao";
import { healthCheck, isNetworkError } from "../services/api";
import { listarAlertas } from "../services/alertaService";
import { buscarPainel } from "../services/painelService";
import { carregarCache, salvarCache } from "../services/cacheService";
import { notificarNovos, prepararNotificacoes } from "../services/notificacaoService";
import { mesclarAlertas, ordenarPorPrioridade } from "../utils/alertas";
import { calcularBackoff } from "../utils/backoff";
import { mensagemErroApi } from "../utils/apiErro";

export const INTERVALO_POLLING_MS = 5000;
// A cada 12 ciclos (~1 min) busca a lista completa, para corrigir qualquer diferença
const CICLOS_PARA_SINCRONIA_COMPLETA = 12;
// Margem no ?desde= para não perder evento salvo no mesmo instante da consulta
const MARGEM_DESDE_MS = 2000;
const TEMPO_BANNER_RECONECTADO_MS = 3000;
const TEMPO_BANNER_ALERTA_MS = 10000;

export type EstadoConexao = {
  status: StatusConexao;
  alertas: Alerta[];
  painel: Painel | null;
  ultimaAtualizacao: string | null;
  tentativas: number;
  proximaTentativaMs: number | null;
  mostrarReconectado: boolean;
  alertaDestaque: Alerta | null;
  latenciaMs: number | null;
  atrasoAlertaMs: number | null;
  erro: string | null;
};

export type ConexaoContextValor = EstadoConexao & {
  atualizarAgora: () => Promise<void>;
  dispensarDestaque: () => void;
  aplicarAlteracao: (alerta: Alerta) => void;
};

const estadoInicial: EstadoConexao = {
  status: "carregando",
  alertas: [],
  painel: null,
  ultimaAtualizacao: null,
  tentativas: 0,
  proximaTentativaMs: null,
  mostrarReconectado: false,
  alertaDestaque: null,
  latenciaMs: null,
  atrasoAlertaMs: null,
  erro: null,
};

export const ConexaoContext = createContext<ConexaoContextValor | null>(null);

type Props = {
  children: React.ReactNode;
  intervaloMs?: number;
};

export function ConexaoProvider({ children, intervaloMs = INTERVALO_POLLING_MS }: Props) {
  const [estado, setEstado] = useState<EstadoConexao>(estadoInicial);

  const timer = useRef<ReturnType<typeof setTimeout> | null>(null);
  const timerBanner = useRef<ReturnType<typeof setTimeout> | null>(null);
  const timerDestaque = useRef<ReturnType<typeof setTimeout> | null>(null);
  const montado = useRef(true);
  const statusRef = useRef<StatusConexao>("carregando");
  const tentativasRef = useRef(0);
  const ciclosRef = useRef(0);
  const desdeRef = useRef<string | null>(null);
  const alertasRef = useRef<Alerta[]>([]);
  const ocupado = useRef(false);

  const atualizarEstado = useCallback((parcial: Partial<EstadoConexao>) => {
    if (montado.current) {
      setEstado((anterior) => ({ ...anterior, ...parcial }));
    }
  }, []);

  const destacar = useCallback(
    (alerta: Alerta) => {
      atualizarEstado({ alertaDestaque: alerta });
      if (timerDestaque.current) clearTimeout(timerDestaque.current);
      timerDestaque.current = setTimeout(() => atualizarEstado({ alertaDestaque: null }), TEMPO_BANNER_ALERTA_MS);
    },
    [atualizarEstado]
  );

  // Busca no servidor. Lança o erro para quem chamou decidir (online/offline).
  const sincronizar = useCallback(
    async (completa: boolean) => {
      const inicio = Date.now();
      let desde: string | undefined;
      if (!completa && desdeRef.current) {
        desde = new Date(new Date(desdeRef.current).getTime() - MARGEM_DESDE_MS).toISOString();
      }
      const [pagina, painel] = await Promise.all([listarAlertas({ desde }), buscarPainel()]);
      const latenciaMs = Date.now() - inicio;

      const anteriores = new Set(alertasRef.current.map((a) => a.id));
      const alertas = completa
        ? ordenarPorPrioridade(pagina.conteudo.filter((a) => a.status === "ATIVO"))
        : mesclarAlertas(alertasRef.current, pagina.conteudo);
      alertasRef.current = alertas;
      desdeRef.current = pagina.geradoEm;
      ciclosRef.current += 1;

      // Indicador "tempo de atualização": quanto o alerta levou do servidor até a tela
      const chegaram = alertas.filter((a) => !anteriores.has(a.id));
      let atrasoAlertaMs: number | null = null;
      if (!completa && chegaram.length > 0 && anteriores.size > 0) {
        const atrasos = chegaram.map((a) => Math.max(0, Date.now() - new Date(a.atualizadoEm).getTime()));
        atrasoAlertaMs = Math.round(atrasos.reduce((s, v) => s + v, 0) / atrasos.length);
      }

      const ultimaAtualizacao = new Date().toISOString();
      atualizarEstado({
        alertas,
        painel,
        ultimaAtualizacao,
        latenciaMs,
        erro: null,
        ...(atrasoAlertaMs !== null ? { atrasoAlertaMs } : {}),
      });
      await salvarCache({ alertas, painel, ultimaAtualizacao });

      const novos = await notificarNovos(alertas);
      if (novos.length > 0) {
        destacar(ordenarPorPrioridade(novos)[0]);
      }
    },
    [atualizarEstado, destacar]
  );

  const agendar = useCallback((ms: number) => {
    if (timer.current) clearTimeout(timer.current);
    if (!montado.current) return;
    timer.current = setTimeout(() => {
      void cicloRef.current();
    }, ms);
  }, []);

  const entrarOffline = useCallback(() => {
    const espera = calcularBackoff(tentativasRef.current);
    statusRef.current = "offline";
    atualizarEstado({ status: "offline", tentativas: tentativasRef.current, proximaTentativaMs: espera });
    agendar(espera);
  }, [agendar, atualizarEstado]);

  const tentarReconectar = useCallback(async () => {
    statusRef.current = "reconectando";
    atualizarEstado({ status: "reconectando" });
    const servidorNoAr = await healthCheck();
    if (servidorNoAr) {
      try {
        await sincronizar(true);
        statusRef.current = "online";
        tentativasRef.current = 0;
        atualizarEstado({ status: "online", tentativas: 0, proximaTentativaMs: null, mostrarReconectado: true });
        if (timerBanner.current) clearTimeout(timerBanner.current);
        timerBanner.current = setTimeout(
          () => atualizarEstado({ mostrarReconectado: false }),
          TEMPO_BANNER_RECONECTADO_MS
        );
        agendar(intervaloMs);
        return;
      } catch {
        // servidor respondeu o health mas falhou na busca: conta como nova tentativa
      }
    }
    tentativasRef.current += 1;
    entrarOffline();
  }, [agendar, atualizarEstado, entrarOffline, intervaloMs, sincronizar]);

  const ciclo = useCallback(async () => {
    if (ocupado.current) return;
    ocupado.current = true;
    try {
      if (statusRef.current === "offline" || statusRef.current === "reconectando") {
        await tentarReconectar();
        return;
      }
      const completa = ciclosRef.current % CICLOS_PARA_SINCRONIA_COMPLETA === 0;
      await sincronizar(completa);
      if (statusRef.current !== "online") {
        statusRef.current = "online";
        atualizarEstado({ status: "online" });
      }
      agendar(intervaloMs);
    } catch (erro) {
      if (isNetworkError(erro)) {
        tentativasRef.current = 0;
        entrarOffline();
      } else {
        // Servidor respondeu com erro: continua online e tenta de novo no próximo ciclo
        atualizarEstado({ erro: mensagemErroApi(erro, "Não foi possível atualizar os alertas.") });
        agendar(intervaloMs);
      }
    } finally {
      ocupado.current = false;
    }
  }, [agendar, atualizarEstado, entrarOffline, intervaloMs, sincronizar, tentarReconectar]);

  // Referência estável para o timer sempre chamar a versão atual do ciclo
  const cicloRef = useRef(ciclo);
  cicloRef.current = ciclo;

  useEffect(() => {
    montado.current = true;
    (async () => {
      const cache = await carregarCache();
      if (cache && montado.current) {
        alertasRef.current = cache.alertas ?? [];
        atualizarEstado({
          alertas: cache.alertas ?? [],
          painel: cache.painel,
          ultimaAtualizacao: cache.ultimaAtualizacao,
        });
      }
      await prepararNotificacoes();
      await cicloRef.current();
    })();
    return () => {
      montado.current = false;
      [timer, timerBanner, timerDestaque].forEach((t) => t.current && clearTimeout(t.current));
    };
  }, [atualizarEstado]);

  // Pull-to-refresh: se estiver offline, já tenta reconectar
  const atualizarAgora = useCallback(async () => {
    if (timer.current) clearTimeout(timer.current);
    await cicloRef.current();
  }, []);

  const dispensarDestaque = useCallback(() => atualizarEstado({ alertaDestaque: null }), [atualizarEstado]);

  // Após confirmar ou resolver, atualiza a tela sem esperar o próximo ciclo
  const aplicarAlteracao = useCallback(
    (alerta: Alerta) => {
      const alertas = mesclarAlertas(alertasRef.current, [alerta]);
      alertasRef.current = alertas;
      atualizarEstado({ alertas });
    },
    [atualizarEstado]
  );

  const valor = useMemo(
    () => ({ ...estado, atualizarAgora, dispensarDestaque, aplicarAlteracao }),
    [estado, atualizarAgora, dispensarDestaque, aplicarAlteracao]
  );

  return <ConexaoContext.Provider value={valor}>{children}</ConexaoContext.Provider>;
}
