import { TipoRisco } from "../types/tipoRisco";
import { Origem } from "../types/origem";

type InfoTipo = {
  texto: string;
  icone: "water" | "sunny" | "flash-off";
};

const TIPOS: Record<TipoRisco, InfoTipo> = {
  ALAGAMENTO: { texto: "Alagamento", icone: "water" },
  ILHA_DE_CALOR: { texto: "Calor extremo", icone: "sunny" },
  INTERRUPCAO_SERVICO: { texto: "Falta de serviço", icone: "flash-off" },
};

export const TIPOS_RISCO: TipoRisco[] = ["ALAGAMENTO", "ILHA_DE_CALOR", "INTERRUPCAO_SERVICO"];

export function infoTipo(tipo: TipoRisco): InfoTipo {
  return TIPOS[tipo] ?? TIPOS.ALAGAMENTO;
}

const ORIGENS: Record<Origem, { texto: string; icone: "hardware-chip" | "business" | "people" }> = {
  SENSOR: { texto: "Sensor", icone: "hardware-chip" },
  BASE_PUBLICA: { texto: "Órgão público", icone: "business" },
  COMUNITARIO: { texto: "Moradores", icone: "people" },
};

export function infoOrigem(origem: Origem) {
  return ORIGENS[origem] ?? ORIGENS.SENSOR;
}

// Confiança em palavras, para quem não quer ler porcentagem
export function textoConfianca(confianca: number): string {
  if (confianca >= 80) return "confiança alta";
  if (confianca >= 55) return "confiança média";
  return "confiança baixa";
}
