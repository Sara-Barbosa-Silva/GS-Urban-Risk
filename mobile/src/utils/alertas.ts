import { Alerta } from "../interfaces/alerta";

// Mesma ordem da API: maior prioridade primeiro; empate, o mais recente
export function ordenarPorPrioridade(alertas: Alerta[]): Alerta[] {
  return [...alertas].sort(
    (a, b) =>
      b.scorePrioridade - a.scorePrioridade ||
      new Date(b.atualizadoEm).getTime() - new Date(a.atualizadoEm).getTime()
  );
}

/**
 * Junta o que mudou (consulta com ?desde=) com o que já estava na tela.
 * Alertas resolvidos saem da lista.
 */
export function mesclarAlertas(atuais: Alerta[], mudancas: Alerta[]): Alerta[] {
  const mapa = new Map<number, Alerta>();
  atuais.forEach((a) => mapa.set(a.id, a));
  mudancas.forEach((m) => {
    if (m.status === "RESOLVIDO") {
      mapa.delete(m.id);
    } else {
      mapa.set(m.id, m);
    }
  });
  return ordenarPorPrioridade(Array.from(mapa.values()));
}
