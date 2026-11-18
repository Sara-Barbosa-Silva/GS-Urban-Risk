/**
 * BotaoGrande - botão principal com ícone, texto e área de toque generosa
 */
import React from "react";
import { Pressable, Text, ActivityIndicator } from "react-native";
import { Ionicons } from "@expo/vector-icons";

import { botaoGrandeStyles as styles, cores } from "../styles";

type Props = {
  texto: string;
  onPress: () => void;
  icone?: keyof typeof Ionicons.glyphMap;
  secundario?: boolean;
  carregando?: boolean;
  desabilitado?: boolean;
  dica?: string;
};

export default function BotaoGrande({ texto, onPress, icone, secundario, carregando, desabilitado, dica }: Props) {
  const corConteudo = secundario ? cores.primaria : cores.branco;
  const bloqueado = desabilitado || carregando;
  return (
    <Pressable
      style={[styles.botao, secundario && styles.secundario, bloqueado && styles.desabilitado]}
      onPress={onPress}
      disabled={bloqueado}
      accessibilityRole="button"
      accessibilityLabel={texto}
      accessibilityHint={dica}
      accessibilityState={{ disabled: !!bloqueado, busy: !!carregando }}
    >
      {carregando ? (
        <ActivityIndicator color={corConteudo} />
      ) : icone ? (
        <Ionicons name={icone} size={24} color={corConteudo} accessible={false} />
      ) : null}
      <Text style={[styles.texto, secundario && styles.textoSecundario]}>{texto}</Text>
    </Pressable>
  );
}
