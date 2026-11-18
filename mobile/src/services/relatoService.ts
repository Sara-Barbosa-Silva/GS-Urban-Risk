import api from "./api";
import { Alerta } from "../interfaces/alerta";
import { NovoRelato, Relato } from "../interfaces/relato";
import { idDispositivo } from "../utils/dispositivo";

export async function enviarRelato(relato: NovoRelato): Promise<Relato> {
  const payload = {
    ...relato,
    descricao: relato.descricao?.trim() || undefined,
    contato: relato.contato?.trim() || undefined,
  };
  const response = await api.post<Relato>("/api/relatos", payload);
  return response.data;
}

// Outro morador confirma: a confiança do alerta sobe
export async function confirmarRelato(relatoId: number): Promise<Alerta> {
  const dispositivo = await idDispositivo();
  const response = await api.post<Alerta>(`/api/relatos/${relatoId}/confirmar`, null, {
    headers: { "X-Dispositivo-Id": dispositivo },
  });
  return response.data;
}
