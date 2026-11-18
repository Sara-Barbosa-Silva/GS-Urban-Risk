/**
 * BannerNovoAlerta - aviso dentro do app quando chega alerta alto ou crítico.
 * Garante o aviso mesmo onde a notificação do sistema não aparece (ex.: Expo Web).
 */
import React from "react";
import { View, Text, Pressable } from "react-native";
import { Ionicons } from "@expo/vector-icons";

import { Alerta } from "../interfaces/alerta";
import { infoNivel } from "../utils/nivel";
import { bannerNovoAlertaStyles as styles } from "../styles";

type Props = {
  alerta: Alerta | null;
  onAbrir: (alerta: Alerta) => void;
  onFechar: () => void;
};

export default function BannerNovoAlerta({ alerta, onAbrir, onFechar }: Props) {
  if (!alerta) {
    return null;
  }
  const nivel = infoNivel(alerta.nivelAlerta);
  return (
    <View style={[styles.banner, { backgroundColor: nivel.cor }]} accessibilityLiveRegion="assertive">
      <Pressable
        style={styles.conteudo}
        onPress={() => onAbrir(alerta)}
        accessibilityRole="button"
        accessibilityLabel={`Novo alerta. ${nivel.texto}: ${alerta.titulo}. Toque para ver detalhes.`}
      >
        <Text style={[styles.rotulo, { color: nivel.corTexto }]}>
          Novo alerta · {nivel.texto}
        </Text>
        <Text style={[styles.titulo, { color: nivel.corTexto }]}>{alerta.titulo}</Text>
      </Pressable>
      <Pressable
        style={styles.fechar}
        onPress={onFechar}
        accessibilityRole="button"
        accessibilityLabel="Fechar aviso"
      >
        <Ionicons name="close" size={26} color={nivel.corTexto} accessible={false} />
      </Pressable>
    </View>
  );
}
