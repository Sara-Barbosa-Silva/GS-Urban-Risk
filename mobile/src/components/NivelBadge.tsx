/**
 * NivelBadge - nível do alerta com cor, ícone e texto (nunca só cor)
 */
import React from "react";
import { View, Text } from "react-native";
import { Ionicons } from "@expo/vector-icons";

import { NivelAlerta } from "../types/nivelAlerta";
import { infoNivel } from "../utils/nivel";
import { nivelBadgeStyles as styles } from "../styles";

type Props = {
  nivel: NivelAlerta;
};

export default function NivelBadge({ nivel }: Props) {
  const info = infoNivel(nivel);
  return (
    <View
      style={[styles.badge, { backgroundColor: info.cor }]}
      accessible
      accessibilityRole="text"
      accessibilityLabel={info.texto}
    >
      <Ionicons name={info.icone} size={18} color={info.corTexto} accessible={false} />
      <Text style={[styles.texto, { color: info.corTexto }]}>{info.texto.toUpperCase()}</Text>
    </View>
  );
}
