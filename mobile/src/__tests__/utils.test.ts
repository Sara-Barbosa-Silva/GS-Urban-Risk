import { formatarHaQuanto, formatarHora, segundosDesde } from "../utils/tempo";
import { infoNivel, deveNotificar } from "../utils/nivel";
import { calcularBackoff } from "../utils/backoff";
import { mesclarAlertas, ordenarPorPrioridade } from "../utils/alertas";
import { textoConfianca } from "../utils/tipoRisco";
import { mensagemErroApi } from "../utils/apiErro";
import { Alerta } from "../interfaces/alerta";

const AGORA = new Date("2026-11-19T15:00:00Z").getTime();

function alerta(id: number, score: number, extra: Partial<Alerta> = {}): Alerta {
  return {
    id,
    tipoRisco: "ALAGAMENTO",
    tipoRiscoDescricao: "Alagamento",
    regiaoId: 1,
    regiaoNome: "Mooca",
    titulo: `Alerta ${id}`,
    origem: "SENSOR",
    nivelConfianca: 85,
    fonte: "Sensor PLU-02 · Mooca",
    severidade: 0.5,
    scorePrioridade: score,
    nivelAlerta: "ALTO",
    status: "ATIVO",
    criadoEm: "2026-11-19T14:00:00Z",
    atualizadoEm: "2026-11-19T14:59:00Z",
    confirmacoes: 0,
    origensCorroborando: 1,
    ...extra,
  };
}

describe("formatarHaQuanto", () => {
  it("usa linguagem simples", () => {
    expect(formatarHaQuanto("2026-11-19T14:59:58Z", AGORA)).toBe("agora");
    expect(formatarHaQuanto("2026-11-19T14:59:30Z", AGORA)).toBe("há 30s");
    expect(formatarHaQuanto("2026-11-19T14:55:00Z", AGORA)).toBe("há 5 min");
    expect(formatarHaQuanto("2026-11-19T12:00:00Z", AGORA)).toBe("há 3 h");
    expect(formatarHaQuanto("2026-11-18T14:00:00Z", AGORA)).toBe("há 1 dia");
    expect(formatarHaQuanto(null, AGORA)).toBe("sem dados");
  });

  it("calcula segundos desde a atualização", () => {
    expect(segundosDesde("2026-11-19T14:59:48Z", AGORA)).toBe(12);
    expect(segundosDesde(null, AGORA)).toBeNull();
  });

  it("formata hora com dois dígitos", () => {
    expect(formatarHora(null)).toBe("--:--");
    expect(formatarHora("2026-11-19T15:05:00Z")).toMatch(/^\d{2}:\d{2}$/);
  });
});

describe("nível do alerta", () => {
  it("tem texto e cor para cada nível", () => {
    expect(infoNivel("BAIXO").texto).toBe("Risco baixo");
    expect(infoNivel("MODERADO").texto).toBe("Risco moderado");
    expect(infoNivel("ALTO").texto).toBe("Risco alto");
    expect(infoNivel("CRITICO").texto).toBe("Risco crítico");
    expect(infoNivel("BAIXO").cor).toBe("#2E7D32");
    expect(infoNivel("CRITICO").cor).toBe("#B71C1C");
  });

  it("só alto e crítico notificam", () => {
    expect(deveNotificar("CRITICO")).toBe(true);
    expect(deveNotificar("ALTO")).toBe(true);
    expect(deveNotificar("MODERADO")).toBe(false);
    expect(deveNotificar("BAIXO")).toBe(false);
  });

  it("confiança em palavras", () => {
    expect(textoConfianca(90)).toBe("confiança alta");
    expect(textoConfianca(60)).toBe("confiança média");
    expect(textoConfianca(40)).toBe("confiança baixa");
  });
});

describe("calcularBackoff", () => {
  it("dobra a espera até 30 segundos", () => {
    expect(calcularBackoff(0)).toBe(5000);
    expect(calcularBackoff(1)).toBe(10000);
    expect(calcularBackoff(2)).toBe(20000);
    expect(calcularBackoff(3)).toBe(30000);
    expect(calcularBackoff(10)).toBe(30000);
    expect(calcularBackoff(-1)).toBe(5000);
  });
});

describe("mesclarAlertas", () => {
  it("ordena por prioridade", () => {
    const lista = ordenarPorPrioridade([alerta(1, 40), alerta(2, 90), alerta(3, 60)]);
    expect(lista.map((a) => a.id)).toEqual([2, 3, 1]);
  });

  it("atualiza, adiciona e remove resolvidos", () => {
    const atuais = [alerta(1, 40), alerta(2, 90)];
    const mudancas = [alerta(1, 95), alerta(2, 90, { status: "RESOLVIDO" }), alerta(3, 50)];
    const resultado = mesclarAlertas(atuais, mudancas);
    expect(resultado.map((a) => a.id)).toEqual([1, 3]);
    expect(resultado[0].scorePrioridade).toBe(95);
  });
});

describe("mensagemErroApi", () => {
  it("lê o formato {erro} do backend", () => {
    expect(mensagemErroApi({ response: { data: { erro: "Escolha a região." } } }, "falhou")).toBe("Escolha a região.");
    expect(mensagemErroApi(new Error("x"), "falhou")).toBe("falhou");
  });
});
