import api from "./api";
import { Painel } from "../interfaces/painel";

export async function buscarPainel(): Promise<Painel> {
  const response = await api.get<Painel>("/api/painel");
  return response.data;
}
