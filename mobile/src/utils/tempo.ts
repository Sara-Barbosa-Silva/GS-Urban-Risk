/**
 * Formatação de tempo em linguagem simples ("há 2 min", "às 14:05").
 */
export function formatarHaQuanto(dataIso: string | null | undefined, agora: number = Date.now()): string {
  if (!dataIso) {
    return "sem dados";
  }
  const segundos = Math.max(0, Math.floor((agora - new Date(dataIso).getTime()) / 1000));
  if (segundos < 5) return "agora";
  if (segundos < 60) return `há ${segundos}s`;
  const minutos = Math.floor(segundos / 60);
  if (minutos < 60) return `há ${minutos} min`;
  const horas = Math.floor(minutos / 60);
  if (horas < 24) return `há ${horas} h`;
  const dias = Math.floor(horas / 24);
  return dias === 1 ? "há 1 dia" : `há ${dias} dias`;
}

export function formatarHora(dataIso: string | null | undefined): string {
  if (!dataIso) {
    return "--:--";
  }
  const d = new Date(dataIso);
  const hh = String(d.getHours()).padStart(2, "0");
  const mm = String(d.getMinutes()).padStart(2, "0");
  return `${hh}:${mm}`;
}

export function segundosDesde(dataIso: string | null | undefined, agora: number = Date.now()): number | null {
  if (!dataIso) {
    return null;
  }
  return Math.max(0, Math.floor((agora - new Date(dataIso).getTime()) / 1000));
}
