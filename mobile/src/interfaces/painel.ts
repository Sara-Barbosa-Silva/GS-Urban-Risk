import { NivelAlerta } from "../types/nivelAlerta";
import { TipoRisco } from "../types/tipoRisco";

export interface Leitura {
  id: number;
  codigoSensor: string;
  tipoSensor: "PLUVIOMETRO" | "NIVEL_RIO" | "TEMPERATURA" | "REDE_ELETRICA";
  regiaoNome: string;
  valor: number;
  unidade: string;
  dataHora: string;
  zScore?: number | null;
  anomalia: boolean;
  acimaDoLimite: boolean;
}

export interface Painel {
  totalAtivos: number;
  porNivel: Record<NivelAlerta, number>;
  porTipo: Record<TipoRisco, number>;
  regiaoMaisCritica: {
    id: number;
    nome: string;
    alertasAtivos: number;
    maiorScore: number;
    nivel: NivelAlerta;
  } | null;
  ultimasLeituras: Leitura[];
  ultimaAtualizacao: string | null;
  geradoEm: string;
  simuladorAtivo: boolean;
}
