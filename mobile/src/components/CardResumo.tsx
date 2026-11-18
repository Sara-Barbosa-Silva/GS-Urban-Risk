/**
 * CardResumo - bloco do painel com título e ícone
 */
import React from "react";
import { View, Text } from "react-native";
import { Ionicons } from "@expo/vector-icons";

import { cardResumoStyles as styles, cores } from "../styles";

type Props = {
  titulo: string;
  icone: keyof typeof Ionicons.glyphMap;
  children: React.ReactNode;
};

export default function CardResumo({ titulo, icone, children }: Props) {
  return (
    <View style={styles.card}>
      <View style={styles.cabecalho} accessible accessibilityRole="header" accessibilityLabel={titulo}>
        <Ionicons name={icone} size={22} color={cores.primaria} accessible={false} />
        <Text style={styles.titulo}>{titulo}</Text>
      </View>
      {children}
    </View>
  );
}
