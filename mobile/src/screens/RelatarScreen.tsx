/**
 * RelatarScreen - morador avisa sobre um problema no bairro.
 * Formulário curto, sem nome; contato opcional com aviso de privacidade (LGPD).
 */
import React, { useEffect, useState } from "react";
import { View, Text, TextInput, Pressable, ScrollView, KeyboardAvoidingView, Platform } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";

import { TelaBase, BotaoGrande } from "../components";
import { Regiao } from "../interfaces/regiao";
import { Relato } from "../interfaces/relato";
import { RootStackParamList } from "../navigation/types";
import { TipoRisco } from "../types/tipoRisco";
import { listarRegioes } from "../services/regiaoService";
import { enviarRelato } from "../services/relatoService";
import { marcarComoNotificado } from "../services/notificacaoService";
import { isNetworkError } from "../services/api";
import { useAlertasTempoReal } from "../hooks/useAlertasTempoReal";
import { mensagemErroApi } from "../utils/apiErro";
import { infoTipo, TIPOS_RISCO } from "../utils/tipoRisco";
import { relatarStyles as styles, cores } from "../styles";

export default function RelatarScreen() {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const { atualizarAgora, status } = useAlertasTempoReal();
  const [regioes, setRegioes] = useState<Regiao[]>([]);
  const [tipo, setTipo] = useState<TipoRisco | null>(null);
  const [regiaoId, setRegiaoId] = useState<number | null>(null);
  const [descricao, setDescricao] = useState("");
  const [contato, setContato] = useState("");
  const [enviando, setEnviando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);
  const [enviado, setEnviado] = useState<Relato | null>(null);

  async function carregarRegioes() {
    try {
      setRegioes(await listarRegioes());
    } catch {
      // Sem servidor: a lista aparece quando a conexão voltar
    }
  }

  useEffect(() => {
    carregarRegioes();
  }, []);

  useEffect(() => {
    if (status === "online" && regioes.length === 0) {
      carregarRegioes();
    }
  }, [status, regioes.length]);

  async function enviar() {
    setErro(null);
    if (!tipo) {
      setErro("Escolha o que está acontecendo.");
      return;
    }
    if (!regiaoId) {
      setErro("Escolha o bairro.");
      return;
    }
    setEnviando(true);
    try {
      const relato = await enviarRelato({ tipoRisco: tipo, regiaoId, descricao, contato });
      await marcarComoNotificado(relato.eventoId);
      setEnviado(relato);
      setTipo(null);
      setDescricao("");
      setContato("");
      await atualizarAgora();
    } catch (e) {
      setErro(
        isNetworkError(e)
          ? "Sem conexão com o servidor. Tente de novo em instantes."
          : mensagemErroApi(e, "Não foi possível enviar o relato.")
      );
    } finally {
      setEnviando(false);
    }
  }

  return (
    <TelaBase>
      <KeyboardAvoidingView style={{ flex: 1 }} behavior={Platform.OS === "ios" ? "padding" : undefined}>
        <ScrollView contentContainerStyle={styles.conteudo} keyboardShouldPersistTaps="handled">
          <Text style={styles.titulo} accessibilityRole="header">
            Avisar sobre um problema
          </Text>
          <Text style={styles.explicacao}>
            Seu aviso ajuda vizinhos e a Defesa Civil. Quanto mais pessoas confirmam, mais confiável ele fica.
          </Text>

          <Text style={styles.rotulo}>1. O que está acontecendo?</Text>
          <View style={styles.tipos} accessibilityRole="radiogroup">
            {TIPOS_RISCO.map((t) => {
              const info = infoTipo(t);
              const ativo = tipo === t;
              return (
                <Pressable
                  key={t}
                  style={[styles.tipo, ativo && styles.tipoAtivo]}
                  onPress={() => setTipo(t)}
                  accessibilityRole="radio"
                  accessibilityState={{ checked: ativo }}
                  accessibilityLabel={info.texto}
                >
                  <Ionicons name={info.icone} size={30} color={ativo ? cores.branco : cores.primaria} accessible={false} />
                  <Text style={[styles.tipoTexto, ativo && styles.tipoTextoAtivo]}>{info.texto}</Text>
                </Pressable>
              );
            })}
          </View>

          <Text style={styles.rotulo}>2. Em qual bairro?</Text>
          {regioes.length === 0 ? (
            <Text style={styles.explicacao}>Carregando bairros... (precisa de conexão com o servidor)</Text>
          ) : null}
          <View style={styles.regioes} accessibilityRole="radiogroup">
            {regioes.map((r) => {
              const ativo = regiaoId === r.id;
              return (
                <Pressable
                  key={r.id}
                  style={[styles.regiao, ativo && styles.regiaoAtiva]}
                  onPress={() => setRegiaoId(r.id)}
                  accessibilityRole="radio"
                  accessibilityState={{ checked: ativo }}
                  accessibilityLabel={r.nome}
                >
                  <Text style={[styles.regiaoTexto, ativo && styles.regiaoTextoAtivo]}>{r.nome}</Text>
                </Pressable>
              );
            })}
          </View>

          <Text style={styles.rotulo}>3. Quer contar mais? (opcional)</Text>
          <TextInput
            style={[styles.input, styles.inputGrande]}
            value={descricao}
            onChangeText={setDescricao}
            placeholder="Ex.: água na altura do joelho na rua da escola"
            placeholderTextColor={cores.textoSecundario}
            multiline
            maxLength={500}
            accessibilityLabel="Descrição do problema, opcional"
          />

          <Text style={styles.rotulo}>4. Contato para retorno (opcional)</Text>
          <TextInput
            style={styles.input}
            value={contato}
            onChangeText={setContato}
            placeholder="Telefone ou e-mail"
            placeholderTextColor={cores.textoSecundario}
            autoCapitalize="none"
            maxLength={120}
            accessibilityLabel="Contato para retorno, opcional"
          />
          <View style={styles.privacidade} accessible accessibilityLabel="Aviso de privacidade">
            <Ionicons name="lock-closed" size={22} color={cores.primariaEscura} accessible={false} />
            <Text style={styles.privacidadeTexto}>
              Privacidade (LGPD - Lei 13.709/2018): não pedimos seu nome. Se informar contato, ele é guardado
              criptografado (hash), nunca aparece para outras pessoas e é apagado em 30 dias. O local do aviso aparece
              só como o bairro.
            </Text>
          </View>

          <View style={styles.botao}>
            <BotaoGrande texto="Enviar aviso" icone="send" onPress={enviar} carregando={enviando} />
          </View>

          {erro ? (
            <Text style={styles.erro} accessibilityRole="alert">
              {erro}
            </Text>
          ) : null}

          {enviado ? (
            <View style={styles.sucesso} accessibilityRole="alert">
              <Text style={styles.sucessoTexto}>
                Aviso enviado! Ele já aparece nos alertas de {enviado.regiaoNome} com{" "}
                {enviado.nivelConfianca ?? 40}% de confiança. Peça para um vizinho confirmar.
              </Text>
              <View style={{ marginTop: 10 }}>
                <BotaoGrande
                  texto="Ver meu aviso"
                  icone="eye"
                  secundario
                  onPress={() => navigation.navigate("DetalheAlerta", { alertaId: enviado.eventoId })}
                />
              </View>
            </View>
          ) : null}
        </ScrollView>
      </KeyboardAvoidingView>
    </TelaBase>
  );
}
