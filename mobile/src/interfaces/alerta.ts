import { NivelAlerta } from "../types/nivelAlerta";
import { Origem } from "../types/origem";
import { TipoRisco } from "../types/tipoRisco";

export interface Alerta {
  id: number;
  tipoRisco: TipoRisco;
  tipoRiscoDescricao: string;
  regiaoId: number;
  regiaoNome: string;
  titulo: string;
  descricao?: string | null;
  origem: Origem;
  nivelConfianca: number;
  fonte: string;
  severidade: number;
  scorePrioridade: number;
  nivelAlerta: NivelAlerta;
  motivoPrioridade?: string | null;
  status: "ATIVO" | "RESOLVIDO";
  criadoEm: string;
  atualizadoEm: string;
  latitude?: number | null;
  longitude?: number | null;
  relatoId?: number | null;
  confirmacoes: number;
  origensCorroborando: number;
}

export interface Pagina<T> {
  conteudo: T[];
  pagina: number;
  tamanho: number;
  totalElementos: number;
  totalPaginas: number;
  geradoEm: string;
}
