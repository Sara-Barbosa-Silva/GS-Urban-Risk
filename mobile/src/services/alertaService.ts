import api from "./api";
import { Alerta, Pagina } from "../interfaces/alerta";
import { NivelAlerta } from "../types/nivelAlerta";
import { TipoRisco } from "../types/tipoRisco";

export type FiltroAlertas = {
  tipo?: TipoRisco;
  nivel?: NivelAlerta;
  regiaoId?: number;
  desde?: string;
  size?: number;
};

// Alertas ordenados pela prioridade calculada no backend
export async function listarAlertas(filtro: FiltroAlertas = {}): Promise<Pagina<Alerta>> {
  const response = await api.get<Pagina<Alerta>>("/api/alertas", {
    params: { size: 100, ...filtro },
  });
  return response.data;
}

export async function buscarAlerta(id: number): Promise<Alerta> {
  const response = await api.get<Alerta>(`/api/alertas/${id}`);
  return response.data;
}

// Ação do agente local
export async function resolverAlerta(id: number): Promise<Alerta> {
  const response = await api.patch<Alerta>(`/api/eventos/${id}/resolver`);
  return response.data;
}
