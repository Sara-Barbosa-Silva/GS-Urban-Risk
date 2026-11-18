/**
 * ChipFiltro - botão de filtro grande e fácil de tocar
 */
import React from "react";
import { Pressable, Text } from "react-native";
import { Ionicons } from "@expo/vector-icons";

import { chipFiltroStyles as styles, cores } from "../styles";

type Props = {
  texto: string;
  ativo: boolean;
  onPress: () => void;
  icone?: keyof typeof Ionicons.glyphMap;
  corIcone?: string;
};

export default function ChipFiltro({ texto, ativo, onPress, icone, corIcone }: Props) {
  return (
    <Pressable
      style={[styles.chip, ativo && styles.ativo]}
      onPress={onPress}
      accessibilityRole="button"
      accessibilityState={{ selected: ativo }}
      accessibilityLabel={`Filtrar: ${texto}`}
    >
      {icone ? (
        <Ionicons name={icone} size={18} color={ativo ? cores.branco : corIcone ?? cores.primaria} accessible={false} />
      ) : null}
      <Text style={[styles.texto, ativo && styles.textoAtivo]}>{texto}</Text>
    </Pressable>
  );
}
