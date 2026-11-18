import axios, { AxiosError } from "axios";
import Constants from "expo-constants";
import { Platform } from "react-native";

/**
 * BASE_URL aponta para o backend Spring Boot (porta 8080).
 *
 * Ordem de escolha (não precisa editar código):
 * 1. Variável EXPO_PUBLIC_API_URL (ex.: EXPO_PUBLIC_API_URL=http://192.168.0.10:8080 npx expo start)
 * 2. "expo.extra.apiUrl" no app.json, se preenchido
 * 3. Celular físico com Expo Go: usa o IP da máquina que está rodando o Expo
 * 4. Emulador Android: 10.0.2.2 (é o localhost da máquina vista pelo emulador)
 * 5. Expo Web e simulador iOS: localhost
 */
function resolverBaseUrl(): string {
  const env = process.env.EXPO_PUBLIC_API_URL;
  if (env) {
    return env;
  }
  const extra = Constants.expoConfig?.extra?.apiUrl as string | undefined;
  if (extra) {
    return extra;
  }
  const hostUri = Constants.expoConfig?.hostUri;
  if (Platform.OS !== "web" && hostUri) {
    const host = hostUri.split(":")[0];
    if (host && host !== "localhost" && host !== "127.0.0.1") {
      return `http://${host}:8080`;
    }
  }
  if (Platform.OS === "android") {
    return "http://10.0.2.2:8080";
  }
  return "http://localhost:8080";
}

export const BASE_URL = resolverBaseUrl();

// Timeout curto: o app percebe rápido quando o servidor cai
const api = axios.create({
  baseURL: BASE_URL,
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
});

export default api;

/**
 * Verifica se o erro é causado por ausência de conexão com o servidor
 * (backend offline, sem internet, timeout, etc).
 *
 * Retorna true quando não há resposta HTTP - ou seja, a requisição
 * nem chegou ao servidor ou ele não respondeu dentro do timeout.
 */
export function isNetworkError(error: unknown): boolean {
  if (error instanceof AxiosError) {
    return !error.response;
  }
  return false;
}

/**
 * Faz um GET em /health e retorna true se o backend estiver acessível.
 * Usa timeout curto (3s) para não travar a reconexão.
 */
export async function healthCheck(): Promise<boolean> {
  try {
    await axios.get(`${BASE_URL}/health`, { timeout: 3000 });
    return true;
  } catch {
    return false;
  }
}
