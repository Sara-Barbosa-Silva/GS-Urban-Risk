/**
 * InicioScreen - escolha do perfil: morador ou agente local
 */
import React from "react";
import { View, Text, Pressable, ScrollView } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { NativeStackScreenProps } from "@react-navigation/native-stack";

import { RootStackParamList } from "../navigation/types";
import { usePerfil } from "../context/PerfilContext";
import { Perfil } from "../types/perfil";
import { inicioStyles as styles, cores } from "../styles";

type Props = NativeStackScreenProps<RootStackParamList, "Inicio">;

export default function InicioScreen({ navigation }: Props) {
  const { escolherPerfil } = usePerfil();

  function entrar(perfil: Perfil) {
    escolherPerfil(perfil);
    navigation.replace("Principal");
  }

  return (
    <View style={styles.container}>
      <ScrollView contentContainerStyle={styles.conteudo}>
        <Ionicons name="shield-checkmark" size={64} color={cores.branco} style={styles.icone} accessible={false} />
        <Text style={styles.titulo} accessibilityRole="header">
          Urban Risk
        </Text>
        <Text style={styles.subtitulo}>Alertas de alagamento, calor extremo e falta de serviços na sua região</Text>

        <Text style={styles.pergunta}>Como você vai usar o app?</Text>

        <Pressable
          style={styles.opcao}
          onPress={() => entrar("morador")}
          accessibilityRole="button"
          accessibilityLabel="Sou morador. Acompanhar alertas e avisar sobre problemas no bairro."
        >
          <Ionicons name="home" size={36} color={cores.primaria} accessible={false} />
          <View style={styles.opcaoTextos}>
            <Text style={styles.opcaoTitulo}>Sou morador</Text>
            <Text style={styles.opcaoDescricao}>Acompanhar alertas e avisar sobre problemas no bairro</Text>
          </View>
          <Ionicons name="chevron-forward" size={28} color={cores.primaria} accessible={false} />
        </Pressable>

        <Pressable
          style={styles.opcao}
          onPress={() => entrar("agente")}
          accessibilityRole="button"
          accessibilityLabel="Sou agente local. Acompanhar a região e marcar ocorrências como resolvidas."
        >
          <Ionicons name="construct" size={36} color={cores.primaria} accessible={false} />
          <View style={styles.opcaoTextos}>
            <Text style={styles.opcaoTitulo}>Sou agente local</Text>
            <Text style={styles.opcaoDescricao}>Acompanhar a região e marcar ocorrências como resolvidas</Text>
          </View>
          <Ionicons name="chevron-forward" size={28} color={cores.primaria} accessible={false} />
        </Pressable>

        <Text style={styles.rodape}>Não pedimos nome nem senha.</Text>
      </ScrollView>
    </View>
  );
}
