import { useEffect, useState } from "react";

// Relógio que atualiza a cada segundo: mantém o "atualizado há Xs" correndo na tela
export function useAgora(intervaloMs = 1000): number {
  const [agora, setAgora] = useState(Date.now());
  useEffect(() => {
    const id = setInterval(() => setAgora(Date.now()), intervaloMs);
    return () => clearInterval(id);
  }, [intervaloMs]);
  return agora;
}
