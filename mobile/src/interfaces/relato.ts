import { TipoRisco } from "../types/tipoRisco";

export interface NovoRelato {
  tipoRisco: TipoRisco;
  regiaoId: number;
  descricao?: string;
  contato?: string;
}

export interface Relato {
  id: number;
  tipoRisco: TipoRisco;
  regiaoId: number;
  regiaoNome: string;
  descricao?: string | null;
  confirmacoes: number;
  contatoInformado: boolean;
  criadoEm: string;
  eventoId: number;
  nivelConfianca?: number | null;
}
