import AsyncStorage from "@react-native-async-storage/async-storage";

import { Alerta } from "../interfaces/alerta";
import { Painel } from "../interfaces/painel";

const CHAVE = "urbanrisk:cache";

export type CacheDados = {
  alertas: Alerta[];
  painel: Painel | null;
  ultimaAtualizacao: string | null;
};

/**
 * Último dado recebido do servidor. É o que aparece quando o app abre
 * sem conexão ou quando o backend cai.
 */
export async function salvarCache(dados: CacheDados): Promise<void> {
  try {
    await AsyncStorage.setItem(CHAVE, JSON.stringify(dados));
  } catch {
    // Sem espaço ou sem permissão: o app segue funcionando só com memória
  }
}

export async function carregarCache(): Promise<CacheDados | null> {
  try {
    const texto = await AsyncStorage.getItem(CHAVE);
    return texto ? (JSON.parse(texto) as CacheDados) : null;
  } catch {
    return null;
  }
}
