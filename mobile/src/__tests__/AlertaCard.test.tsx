import React from "react";
import { fireEvent, render, screen } from "@testing-library/react-native";

import AlertaCard from "../components/AlertaCard";
import { Alerta } from "../interfaces/alerta";

const alerta: Alerta = {
  id: 7,
  tipoRisco: "ALAGAMENTO",
  tipoRiscoDescricao: "Alagamento",
  regiaoId: 2,
  regiaoNome: "Mooca",
  titulo: "Chuva forte e risco de alagamento na Mooca",
  origem: "COMUNITARIO",
  nivelConfianca: 70,
  fonte: "Relato comunitário · 3 confirmações",
  severidade: 0.6,
  scorePrioridade: 78.5,
  nivelAlerta: "CRITICO",
  motivoPrioridade: "Relato confirmado por 3 vizinhos + confiança 70%",
  status: "ATIVO",
  criadoEm: "2026-11-19T14:50:00Z",
  atualizadoEm: "2026-11-19T14:58:00Z",
  relatoId: 3,
  confirmacoes: 3,
  origensCorroborando: 1,
};

describe("AlertaCard", () => {
  it("mostra nível em texto, origem, confiança e motivo", () => {
    render(<AlertaCard alerta={alerta} agora={new Date("2026-11-19T15:00:00Z").getTime()} />);
    expect(screen.getByText("RISCO CRÍTICO")).toBeTruthy();
    expect(screen.getByText("Chuva forte e risco de alagamento na Mooca")).toBeTruthy();
    expect(screen.getByText("Moradores · 70% de confiança")).toBeTruthy();
    expect(screen.getByText(/Por que é prioridade: Relato confirmado/)).toBeTruthy();
    expect(screen.getByText("há 2 min")).toBeTruthy();
  });

  it("é acessível e chama onPress", () => {
    const onPress = jest.fn();
    render(<AlertaCard alerta={alerta} onPress={onPress} />);
    const botao = screen.getByRole("button");
    expect(botao.props.accessibilityLabel).toContain("Risco crítico");
    expect(botao.props.accessibilityLabel).toContain("70 por cento");
    fireEvent.press(botao);
    expect(onPress).toHaveBeenCalledTimes(1);
  });
});
