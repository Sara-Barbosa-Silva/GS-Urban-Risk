import { useContext } from "react";

import { ConexaoContext, ConexaoContextValor } from "../context/ConexaoContext";

/**
 * Estado em tempo real: status da conexão (online | offline | reconectando),
 * alertas, painel e horário da última atualização.
 * Todas as telas leem daqui, então o banner de conexão aparece em qualquer aba.
 */
export function useAlertasTempoReal(): ConexaoContextValor {
  const contexto = useContext(ConexaoContext);
  if (!contexto) {
    throw new Error("useAlertasTempoReal precisa estar dentro de <ConexaoProvider>");
  }
  return contexto;
}
