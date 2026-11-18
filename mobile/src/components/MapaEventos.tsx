/**
 * MapaEventos (versão web) - o react-native-maps não roda no navegador.
 * Mostra um aviso e os alertas agrupados por região, sem quebrar o app.
 * No Android/iOS o Metro usa MapaEventos.native.tsx.
 */
import React from "react";
import { View, Text, ScrollView } from "react-native";
import { Ionicons } from "@expo/vector-icons";

import { Alerta } from "../interfaces/alerta";
import AlertaCard from "./AlertaCard";
import { mapaEventosStyles as styles, cores } from "../styles";

export type MapaEventosProps = {
  alertas: Alerta[];
  onSelecionar: (alerta: Alerta) => void;
};

export function agruparPorRegiao(alertas: Alerta[]): [string, Alerta[]][] {
  const grupos = new Map<string, Alerta[]>();
  alertas.forEach((a) => grupos.set(a.regiaoNome, [...(grupos.get(a.regiaoNome) ?? []), a]));
  // Regiões com o alerta mais prioritário primeiro
  return Array.from(grupos.entries()).sort(
    (x, y) => Math.max(...y[1].map((a) => a.scorePrioridade)) - Math.max(...x[1].map((a) => a.scorePrioridade))
  );
}

export default function MapaEventos({ alertas, onSelecionar }: MapaEventosProps) {
  const grupos = agruparPorRegiao(alertas);
  return (
    <ScrollView contentContainerStyle={{ paddingBottom: 24 }}>
      <View style={styles.aviso} accessibilityRole="text">
        <Ionicons name="map" size={24} color={cores.primariaEscura} accessible={false} />
        <Text style={styles.avisoTexto}>
          O mapa interativo funciona no celular (Expo Go). No navegador, os alertas aparecem agrupados por região.
        </Text>
      </View>
      {grupos.length === 0 ? <Text style={styles.vazio}>Nenhum alerta ativo no momento.</Text> : null}
      {grupos.map(([regiao, lista]) => (
        <View key={regiao} style={styles.grupo}>
          <Text style={styles.grupoTitulo} accessibilityRole="header">
            {regiao} ({lista.length})
          </Text>
          {lista.map((a) => (
            <AlertaCard key={a.id} alerta={a} onPress={() => onSelecionar(a)} />
          ))}
        </View>
      ))}
    </ScrollView>
  );
}
