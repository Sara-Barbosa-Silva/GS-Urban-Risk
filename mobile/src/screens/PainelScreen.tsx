/**
 * PainelScreen - painel de acompanhamento: totais por nível e tipo,
 * região mais crítica, últimas leituras e "atualizado há Xs"
 */
import React, { useState } from "react";
import { View, Text, ScrollView, RefreshControl, ActivityIndicator } from "react-native";
import { Ionicons } from "@expo/vector-icons";

import { TelaBase, CardResumo, NivelBadge } from "../components";
import { useAlertasTempoReal } from "../hooks/useAlertasTempoReal";
import { useAgora } from "../hooks/useAgora";
import { infoNivel, NIVEIS_ORDENADOS } from "../utils/nivel";
import { infoTipo, TIPOS_RISCO } from "../utils/tipoRisco";
import { formatarHaQuanto, segundosDesde } from "../utils/tempo";
import { painelStyles as styles, cores } from "../styles";

const NOME_SENSOR: Record<string, string> = {
  PLUVIOMETRO: "Chuva",
  NIVEL_RIO: "Rio",
  TEMPERATURA: "Temperatura",
  REDE_ELETRICA: "Energia",
};

function formatarValor(valor: number): string {
  return valor.toLocaleString("pt-BR", { maximumFractionDigits: 1 });
}

export default function PainelScreen() {
  const { painel, alertas, status, ultimaAtualizacao, latenciaMs, atrasoAlertaMs, erro, atualizarAgora } =
    useAlertasTempoReal();
  const agora = useAgora();
  const [atualizando, setAtualizando] = useState(false);

  async function puxarParaAtualizar() {
    setAtualizando(true);
    await atualizarAgora();
    setAtualizando(false);
  }

  const segundos = segundosDesde(ultimaAtualizacao, agora);
  const textoAtualizado =
    segundos === null ? "Ainda sem dados" : segundos < 2 ? "Atualizado agora" : `Atualizado há ${segundos}s`;

  return (
    <TelaBase>
      <ScrollView
        contentContainerStyle={styles.conteudo}
        refreshControl={<RefreshControl refreshing={atualizando} onRefresh={puxarParaAtualizar} />}
      >
        <View style={styles.atualizacao} accessible accessibilityLabel={textoAtualizado}>
          <Ionicons
            name={status === "online" ? "radio" : "time"}
            size={18}
            color={status === "online" ? cores.online : cores.textoSecundario}
            accessible={false}
          />
          <Text style={styles.atualizacaoTexto}>
            {textoAtualizado}
            {status === "online" ? " · atualiza sozinho a cada 5s" : ""}
          </Text>
        </View>
        {erro ? <Text style={styles.erro}>{erro}</Text> : null}

        {!painel && status === "carregando" ? (
          <ActivityIndicator size="large" color={cores.primaria} style={styles.carregando} />
        ) : null}

        {painel ? (
          <>
            <CardResumo titulo="Alertas ativos" icone="notifications">
              <View style={styles.totalLinha}>
                <Text style={styles.totalNumero}>{alertas.length}</Text>
                <Text style={styles.totalTexto}>alertas na cidade agora</Text>
              </View>
              {NIVEIS_ORDENADOS.map((nivel) => {
                const qtd = alertas.filter((a) => a.nivelAlerta === nivel).length;
                return (
                  <View
                    key={nivel}
                    style={styles.linhaNivel}
                    accessible
                    accessibilityLabel={`${infoNivel(nivel).texto}: ${qtd}`}
                  >
                    <NivelBadge nivel={nivel} />
                    <Text style={styles.numeroLinha}>{qtd}</Text>
                  </View>
                );
              })}
            </CardResumo>

            <CardResumo titulo="Por tipo de risco" icone="layers">
              {TIPOS_RISCO.map((tipo) => {
                const info = infoTipo(tipo);
                const qtd = alertas.filter((a) => a.tipoRisco === tipo).length;
                return (
                  <View key={tipo} style={styles.linhaNivel} accessible accessibilityLabel={`${info.texto}: ${qtd}`}>
                    <View style={styles.linhaRotulo}>
                      <Ionicons name={info.icone} size={22} color={cores.primaria} accessible={false} />
                      <Text style={styles.textoLinha}>{info.texto}</Text>
                    </View>
                    <Text style={styles.numeroLinha}>{qtd}</Text>
                  </View>
                );
              })}
            </CardResumo>

            <CardResumo titulo="Região mais crítica" icone="location">
              {painel.regiaoMaisCritica ? (
                <View
                  accessible
                  accessibilityLabel={`Região mais crítica: ${painel.regiaoMaisCritica.nome}, ${infoNivel(painel.regiaoMaisCritica.nivel).texto}, ${painel.regiaoMaisCritica.alertasAtivos} alertas`}
                >
                  <Text style={styles.regiaoNome}>{painel.regiaoMaisCritica.nome}</Text>
                  <NivelBadge nivel={painel.regiaoMaisCritica.nivel} />
                  <Text style={styles.textoInfo}>
                    {painel.regiaoMaisCritica.alertasAtivos}{" "}
                    {painel.regiaoMaisCritica.alertasAtivos === 1 ? "alerta ativo" : "alertas ativos"} · prioridade{" "}
                    {Math.round(painel.regiaoMaisCritica.maiorScore)} de 100
                  </Text>
                </View>
              ) : (
                <Text style={styles.textoLinha}>Nenhuma região em alerta.</Text>
              )}
            </CardResumo>

            <CardResumo titulo="Tempo de atualização" icone="speedometer">
              <Text style={styles.textoLinha}>{textoAtualizado}</Text>
              <Text style={styles.textoInfo}>
                Resposta do servidor: {latenciaMs !== null ? `${latenciaMs} ms` : "--"}
              </Text>
              <Text style={styles.textoInfo}>
                Último alerta novo chegou à tela em:{" "}
                {atrasoAlertaMs !== null ? `${(atrasoAlertaMs / 1000).toFixed(1).replace(".", ",")} s` : "aguardando"}
              </Text>
            </CardResumo>

            <CardResumo titulo="Últimas leituras dos sensores" icone="hardware-chip">
              {painel.ultimasLeituras.length === 0 ? (
                <Text style={styles.textoLinha}>Sem leituras recentes.</Text>
              ) : (
                painel.ultimasLeituras.slice(0, 12).map((l) => (
                  <View
                    key={l.codigoSensor}
                    style={styles.leitura}
                    accessible
                    accessibilityLabel={`${NOME_SENSOR[l.tipoSensor]} em ${l.regiaoNome}: ${formatarValor(l.valor)} ${l.unidade}${l.acimaDoLimite ? ", acima do limite" : ""}`}
                  >
                    <Text style={styles.leituraNome}>
                      {NOME_SENSOR[l.tipoSensor]} · {l.regiaoNome}
                    </Text>
                    <Text style={[styles.leituraValor, l.acimaDoLimite && styles.leituraAlerta]}>
                      {l.acimaDoLimite ? "⚠ " : ""}
                      {formatarValor(l.valor)} {l.unidade}
                    </Text>
                  </View>
                ))
              )}
              <Text style={styles.textoInfo}>
                Última leitura {formatarHaQuanto(painel.ultimasLeituras[0]?.dataHora, agora)}
                {painel.simuladorAtivo ? " · sensores simulados" : ""}
              </Text>
            </CardResumo>
          </>
        ) : null}
      </ScrollView>
    </TelaBase>
  );
}
