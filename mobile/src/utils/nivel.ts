import { NivelAlerta } from "../types/nivelAlerta";

type InfoNivel = {
  texto: string;
  cor: string;
  corTexto: string;
  icone: "checkmark-circle" | "alert-circle" | "warning" | "flame";
  ordem: number;
};

// Verde, amarelo, laranja e vermelho, sempre com texto e ícone (nunca só a cor)
const NIVEIS: Record<NivelAlerta, InfoNivel> = {
  BAIXO: { texto: "Risco baixo", cor: "#2E7D32", corTexto: "#FFFFFF", icone: "checkmark-circle", ordem: 1 },
  MODERADO: { texto: "Risco moderado", cor: "#F9A825", corTexto: "#1A1A1A", icone: "alert-circle", ordem: 2 },
  ALTO: { texto: "Risco alto", cor: "#E65100", corTexto: "#FFFFFF", icone: "warning", ordem: 3 },
  CRITICO: { texto: "Risco crítico", cor: "#B71C1C", corTexto: "#FFFFFF", icone: "flame", ordem: 4 },
};

export function infoNivel(nivel: NivelAlerta): InfoNivel {
  return NIVEIS[nivel] ?? NIVEIS.BAIXO;
}

export const NIVEIS_ORDENADOS: NivelAlerta[] = ["CRITICO", "ALTO", "MODERADO", "BAIXO"];

// Só alto e crítico geram notificação
export function deveNotificar(nivel: NivelAlerta): boolean {
  return nivel === "ALTO" || nivel === "CRITICO";
}
