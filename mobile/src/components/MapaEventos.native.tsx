/**
 * MapaEventos (Android/iOS) - marcadores coloridos por nível de alerta.
 * Funciona no Expo Go sem chave de API (Google Maps no Android, Apple Maps no iOS).
 */
import React from "react";
import { View, Text } from "react-native";
import MapView, { Callout, Marker } from "react-native-maps";

import { Alerta } from "../interfaces/alerta";
import { infoNivel, NIVEIS_ORDENADOS } from "../utils/nivel";
import { mapaEventosStyles as styles } from "../styles";

export type MapaEventosProps = {
  alertas: Alerta[];
  onSelecionar: (alerta: Alerta) => void;
};

const SAO_PAULO = { latitude: -23.56, longitude: -46.58, latitudeDelta: 0.38, longitudeDelta: 0.38 };

export default function MapaEventos({ alertas, onSelecionar }: MapaEventosProps) {
  // Alertas da mesma região ficam no centro do bairro: afasta um pouco para não sobrepor
  const contagem = new Map<number, number>();
  const posicionados = alertas
    .filter((a) => a.latitude != null && a.longitude != null)
    .map((a) => {
      const i = contagem.get(a.regiaoId) ?? 0;
      contagem.set(a.regiaoId, i + 1);
      const angulo = i * 1.9;
      const raio = i === 0 ? 0 : 0.004 + i * 0.0015;
      return {
        alerta: a,
        coordenada: {
          latitude: (a.latitude as number) + Math.sin(angulo) * raio,
          longitude: (a.longitude as number) + Math.cos(angulo) * raio,
        },
      };
    });

  return (
    <View style={styles.container}>
      <MapView style={styles.mapa} initialRegion={SAO_PAULO} accessibilityLabel="Mapa de alertas de São Paulo">
        {posicionados.map(({ alerta, coordenada }) => {
          const nivel = infoNivel(alerta.nivelAlerta);
          return (
            <Marker
              key={alerta.id}
              coordinate={coordenada}
              pinColor={nivel.cor}
              title={`${nivel.texto}: ${alerta.regiaoNome}`}
              description={alerta.titulo}
              accessibilityLabel={`${nivel.texto}. ${alerta.titulo}`}
            >
              <Callout onPress={() => onSelecionar(alerta)}>
                <Text>{`${nivel.texto} · ${alerta.regiaoNome}`}</Text>
                <Text>{alerta.titulo}</Text>
                <Text>Toque para ver detalhes</Text>
              </Callout>
            </Marker>
          );
        })}
      </MapView>
      <View style={styles.legenda} accessibilityLabel="Legenda: cores dos níveis de risco">
        {NIVEIS_ORDENADOS.map((n) => (
          <View key={n} style={styles.itemLegenda}>
            <View style={[styles.bolinha, { backgroundColor: infoNivel(n).cor }]} />
            <Text style={styles.textoLegenda}>{infoNivel(n).texto}</Text>
          </View>
        ))}
      </View>
    </View>
  );
}
