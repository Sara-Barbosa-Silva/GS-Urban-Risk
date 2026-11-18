/**
 * Espera entre tentativas de reconexão: 5s, 10s, 20s e depois sempre 30s.
 */
export const BACKOFF_INICIAL_MS = 5000;
export const BACKOFF_MAXIMO_MS = 30000;

export function calcularBackoff(tentativa: number): number {
  const n = Math.max(0, tentativa);
  return Math.min(BACKOFF_MAXIMO_MS, BACKOFF_INICIAL_MS * Math.pow(2, n));
}
