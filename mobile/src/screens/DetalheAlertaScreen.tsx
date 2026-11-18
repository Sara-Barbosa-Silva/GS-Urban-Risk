/**
 * DetalheAlertaScreen - tudo sobre um alerta: o que é, de onde veio,
 * quanto confiar e por que é prioridade. Morador confirma relato; agente resolve.
 */
import React, { useCallback, useEffect, useState } from "react";
import { View, Text, ScrollView } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { NativeStackScreenProps } from "@react-navigation/native-stack";

import { TelaBase, NivelBadge, BotaoGrande } from "../components";
import { Alerta } from "../interfaces/alerta";
import { RootStackParamList } from "../navigation/types";
import { buscarAlerta, resolverAlerta } from "../services/alertaService";
import { confirmarRelato } from "../services/relatoService";
import { isNetworkError } from "../services/api";
import { useAlertasTempoReal } from "../hooks/useAlertasTempoReal";
import { usePerfil } from "../context/PerfilContext";
import { infoNivel } from "../utils/nivel";
import { infoOrigem, infoTipo, textoConfianca } from "../utils/tipoRisco";
import { formatarHaQuanto, formatarHora } from "../utils/tempo";
import { mensagemErroApi } from "../utils/apiErro";
import { detalheAlertaStyles as styles, cores } from "../styles";

type Props = NativeStackScreenProps<RootStackParamList, "DetalheAlerta">;

export default function DetalheAlertaScreen({ route, navigation }: Props) {
  const { alertaId } = route.params;
  const { alertas, aplicarAlteracao } = useAlertasTempoReal();
  const { perfil } = usePerfil();
  const daLista = alertas.find((a) => a.id === alertaId) ?? null;
  const [alerta, setAlerta] = useState<Alerta | null>(daLista);
  const [mensagem, setMensagem] = useState<{ texto: string; ok: boolean } | null>(null);
  const [ocupado, setOcupado] = useState(false);

  const carregar = useCallback(async () => {
    try {
      setAlerta(await buscarAlerta(alertaId));
    } catch {
      // Offline: fica com o que está no cache
    }
  }, [alertaId]);

  useEffect(() => {
    carregar();
  }, [carregar]);

  // Mantém em dia com o polling enquanto a tela está aberta
  useEffect(() => {
    if (daLista) setAlerta(daLista);
  }, [daLista]);

  async function executar(acao: () => Promise<Alerta>, sucesso: (a: Alerta) => string) {
    setOcupado(true);
    setMensagem(null);
    try {
      const atualizado = await acao();
      setAlerta(atualizado);
      aplicarAlteracao(atualizado);
      setMensagem({ texto: sucesso(atualizado), ok: true });
    } catch (e) {
      setMensagem({
        texto: isNetworkError(e)
          ? "Sem conexão com o servidor. Tente quando a conexão voltar."
          : mensagemErroApi(e, "Não foi possível concluir a ação."),
        ok: false,
      });
    } finally {
      setOcupado(false);
    }
  }

  if (!alerta) {
    return (
      <TelaBase>
        <Text style={styles.vazio}>Carregando alerta...</Text>
      </TelaBase>
    );
  }

  const nivel = infoNivel(alerta.nivelAlerta);
  const tipo = infoTipo(alerta.tipoRisco);
  const origem = infoOrigem(alerta.origem);
  const resolvido = alerta.status === "RESOLVIDO";

  return (
    <TelaBase>
      <ScrollView contentContainerStyle={styles.conteudo}>
        <View style={[styles.faixa, { backgroundColor: nivel.cor }]} />
        <NivelBadge nivel={alerta.nivelAlerta} />
        <Text style={styles.titulo} accessibilityRole="header">
          {alerta.titulo}
        </Text>
        {alerta.descricao ? <Text style={styles.descricao}>{alerta.descricao}</Text> : null}

        <View style={styles.secao}>
          <Text style={styles.secaoTitulo}>Onde e quando</Text>
          <View style={styles.linha}>
            <Ionicons name={tipo.icone} size={20} color={cores.primaria} accessible={false} />
            <Text style={styles.texto}>
              {tipo.texto} · {alerta.regiaoNome}
            </Text>
          </View>
          <View style={styles.linha}>
            <Ionicons name="time" size={20} color={cores.primaria} accessible={false} />
            <Text style={styles.texto}>
              Atualizado {formatarHaQuanto(alerta.atualizadoEm)} (às {formatarHora(alerta.atualizadoEm)})
            </Text>
          </View>
          {resolvido ? (
            <View style={styles.linha}>
              <Ionicons name="checkmark-done" size={20} color={cores.online} accessible={false} />
              <Text style={styles.texto}>Situação resolvida</Text>
            </View>
          ) : null}
        </View>

        <View style={styles.secao} accessible accessibilityLabel={`Origem: ${alerta.fonte}. ${textoConfianca(alerta.nivelConfianca)}, ${alerta.nivelConfianca} por cento.`}>
          <Text style={styles.secaoTitulo}>De onde veio e quanto confiar</Text>
          <View style={styles.linha}>
            <Ionicons name={origem.icone} size={20} color={cores.primaria} accessible={false} />
            <Text style={styles.texto}>{alerta.fonte}</Text>
          </View>
          <Text style={[styles.texto, { marginTop: 8 }]}>
            Confiança: {alerta.nivelConfianca}% ({textoConfianca(alerta.nivelConfianca)})
          </Text>
          <View style={styles.barraFundo}>
            <View style={[styles.barra, { width: `${Math.min(100, alerta.nivelConfianca)}%` }]} />
          </View>
          {alerta.origensCorroborando > 1 ? (
            <Text style={[styles.texto, { marginTop: 8 }]}>
              {alerta.origensCorroborando} fontes diferentes apontam este risco na região.
            </Text>
          ) : null}
        </View>

        <View style={styles.secao}>
          <Text style={styles.secaoTitulo}>Por que é prioridade</Text>
          <Text style={styles.texto}>
            Prioridade {Math.round(alerta.scorePrioridade)} de 100 · {nivel.texto}
          </Text>
          {alerta.motivoPrioridade ? (
            <Text style={[styles.texto, { marginTop: 6 }]}>{alerta.motivoPrioridade}</Text>
          ) : null}
        </View>

        {!resolvido ? (
          <View style={styles.acoes}>
            {alerta.origem === "COMUNITARIO" && alerta.relatoId ? (
              <BotaoGrande
                texto="Confirmar este relato"
                icone="thumbs-up"
                carregando={ocupado}
                dica="Diz que você também está vendo este problema"
                onPress={() =>
                  executar(
                    () => confirmarRelato(alerta.relatoId as number),
                    (a) => `Obrigado! Confiança agora em ${a.nivelConfianca}%.`
                  )
                }
              />
            ) : null}
            {perfil === "agente" ? (
              <BotaoGrande
                texto="Marcar como resolvido"
                icone="checkmark-done"
                secundario
                carregando={ocupado}
                onPress={() =>
                  executar(
                    () => resolverAlerta(alerta.id),
                    () => "Alerta marcado como resolvido."
                  )
                }
              />
            ) : null}
          </View>
        ) : (
          <View style={styles.acoes}>
            <BotaoGrande texto="Voltar" icone="arrow-back" secundario onPress={() => navigation.goBack()} />
          </View>
        )}

        {mensagem ? (
          <Text
            style={[styles.mensagem, { color: mensagem.ok ? cores.online : cores.offline }]}
            accessibilityRole="alert"
          >
            {mensagem.texto}
          </Text>
        ) : null}
      </ScrollView>
    </TelaBase>
  );
}
