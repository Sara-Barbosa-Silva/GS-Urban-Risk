import React from "react";
import { act, render, screen } from "@testing-library/react-native";
import { Text } from "react-native";
import { AxiosError } from "axios";

import { ConexaoProvider } from "../context/ConexaoContext";
import { useAlertasTempoReal } from "../hooks/useAlertasTempoReal";
import { Alerta } from "../interfaces/alerta";
import { Painel } from "../interfaces/painel";
import BannerConexao from "../components/BannerConexao";

jest.mock("../services/alertaService", () => ({ listarAlertas: jest.fn() }));
jest.mock("../services/painelService", () => ({ buscarPainel: jest.fn() }));
jest.mock("../services/api", () => {
  const real = jest.requireActual("../services/api");
  return { ...real, healthCheck: jest.fn() };
});

const { listarAlertas } = jest.requireMock("../services/alertaService");
const { buscarPainel } = jest.requireMock("../services/painelService");
const { healthCheck } = jest.requireMock("../services/api");

const alerta: Alerta = {
  id: 1,
  tipoRisco: "ALAGAMENTO",
  tipoRiscoDescricao: "Alagamento",
  regiaoId: 1,
  regiaoNome: "Lapa",
  titulo: "Risco de alagamento na Lapa",
  origem: "SENSOR",
  nivelConfianca: 85,
  fonte: "Sensor PLU-01 · Lapa",
  severidade: 0.7,
  scorePrioridade: 70,
  nivelAlerta: "ALTO",
  status: "ATIVO",
  criadoEm: new Date().toISOString(),
  atualizadoEm: new Date().toISOString(),
  confirmacoes: 0,
  origensCorroborando: 1,
};

const painel: Painel = {
  totalAtivos: 1,
  porNivel: { CRITICO: 0, ALTO: 1, MODERADO: 0, BAIXO: 0 },
  porTipo: { ALAGAMENTO: 1, ILHA_DE_CALOR: 0, INTERRUPCAO_SERVICO: 0 },
  regiaoMaisCritica: null,
  ultimasLeituras: [],
  ultimaAtualizacao: null,
  geradoEm: new Date().toISOString(),
  simuladorAtivo: true,
};

function erroDeRede() {
  return new AxiosError("Network Error", "ERR_NETWORK");
}

function servidorNoAr() {
  listarAlertas.mockResolvedValue({ conteudo: [alerta], geradoEm: new Date().toISOString() });
  buscarPainel.mockResolvedValue(painel);
  healthCheck.mockResolvedValue(true);
}

function servidorFora() {
  listarAlertas.mockRejectedValue(erroDeRede());
  buscarPainel.mockRejectedValue(erroDeRede());
  healthCheck.mockResolvedValue(false);
}

function Sonda() {
  const { status, alertas } = useAlertasTempoReal();
  return (
    <>
      <Text testID="status">{status}</Text>
      <Text testID="total">{alertas.length}</Text>
      <BannerConexao />
    </>
  );
}

async function avancar(ms: number) {
  await act(async () => {
    await jest.advanceTimersByTimeAsync(ms);
  });
}

describe("ConexaoContext (tempo real, queda e reconexão)", () => {
  beforeEach(() => {
    jest.useFakeTimers();
    jest.clearAllMocks();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  it("fica online e carrega alertas quando a API responde", async () => {
    servidorNoAr();
    render(
      <ConexaoProvider>
        <Sonda />
      </ConexaoProvider>
    );
    await avancar(0);
    expect(screen.getByTestId("status").props.children).toBe("online");
    expect(screen.getByTestId("total").props.children).toBe(1);
  });

  it("vai para offline sem perder os dados e volta sozinho para online", async () => {
    servidorNoAr();
    render(
      <ConexaoProvider>
        <Sonda />
      </ConexaoProvider>
    );
    await avancar(0);
    expect(screen.getByTestId("status").props.children).toBe("online");

    // Backend cai
    servidorFora();
    await avancar(5000);
    expect(screen.getByTestId("status").props.children).toBe("offline");
    expect(screen.getByTestId("total").props.children).toBe(1);
    expect(screen.getByText(/Sem conexão com o servidor/)).toBeTruthy();

    // Primeira tentativa (5 s) ainda falha
    await avancar(5000);
    expect(screen.getByTestId("status").props.children).toBe("offline");
    expect(healthCheck).toHaveBeenCalledTimes(1);

    // Backend volta: na próxima tentativa (backoff de 10 s) reconecta
    servidorNoAr();
    await avancar(10000);
    expect(screen.getByTestId("status").props.children).toBe("online");
    expect(screen.getByText(/Conexão restabelecida/)).toBeTruthy();

    // Banner verde some depois de 3 s
    await avancar(3000);
    expect(screen.queryByText(/Conexão restabelecida/)).toBeNull();
  });
});
