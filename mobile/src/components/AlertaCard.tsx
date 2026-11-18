/**
 * AlertaCard - um alerta na lista: nível, título, região, tempo,
 * origem com % de confiança e o motivo da prioridade
 */
import React from "react";
import { View, Text, Pressable } from "react-native";
import { Ionicons } from "@expo/vector-icons";

import { Alerta } from "../interfaces/alerta";
import NivelBadge from "./NivelBadge";
import { infoNivel } from "../utils/nivel";
import { infoOrigem, infoTipo, textoConfianca } from "../utils/tipoRisco";
import { formatarHaQuanto } from "../utils/tempo";
import { alertaCardStyles as styles, cores } from "../styles";

type Props = {
  alerta: Alerta;
  onPress?: () => void;
  agora?: number;
};

export default function AlertaCard({ alerta, onPress, agora }: Props) {
  const nivel = infoNivel(alerta.nivelAlerta);
  const tipo = infoTipo(alerta.tipoRisco);
  const origem = infoOrigem(alerta.origem);
  const quando = formatarHaQuanto(alerta.atualizadoEm, agora);

  return (
    <Pressable
      style={[styles.card, { borderLeftColor: nivel.cor }]}
      onPress={onPress}
      accessibilityRole="button"
      accessibilityLabel={`${nivel.texto}. ${alerta.titulo}. Atualizado ${quando}. Fonte: ${origem.texto}, ${textoConfianca(alerta.nivelConfianca)}, ${alerta.nivelConfianca} por cento.`}
      accessibilityHint="Abre os detalhes do alerta"
    >
      <View style={styles.topo}>
        <NivelBadge nivel={alerta.nivelAlerta} />
        <Text style={styles.tempo}>{quando}</Text>
      </View>

      <Text style={styles.titulo}>{alerta.titulo}</Text>

      <View style={styles.linha}>
        <Ionicons name={tipo.icone} size={18} color={cores.textoSecundario} accessible={false} />
        <Text style={styles.info}>
          {tipo.texto} · {alerta.regiaoNome}
        </Text>
      </View>

      <View style={styles.origem}>
        <Ionicons name={origem.icone} size={18} color={cores.primariaEscura} accessible={false} />
        <Text style={styles.origemTexto}>
          {origem.texto} · {alerta.nivelConfianca}% de confiança
        </Text>
      </View>

      {alerta.motivoPrioridade ? (
        <Text style={styles.motivo}>Por que é prioridade: {alerta.motivoPrioridade}</Text>
      ) : null}
    </Pressable>
  );
}
