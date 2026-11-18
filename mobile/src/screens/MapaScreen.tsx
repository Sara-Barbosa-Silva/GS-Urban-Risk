/**
 * MapaScreen - eventos no mapa (Android/iOS) ou agrupados por região (web)
 */
import React from "react";
import { useNavigation } from "@react-navigation/native";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";

import { TelaBase, MapaEventos } from "../components";
import { useAlertasTempoReal } from "../hooks/useAlertasTempoReal";
import { RootStackParamList } from "../navigation/types";

export default function MapaScreen() {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const { alertas } = useAlertasTempoReal();

  return (
    <TelaBase>
      <MapaEventos
        alertas={alertas}
        onSelecionar={(alerta) => navigation.navigate("DetalheAlerta", { alertaId: alerta.id })}
      />
    </TelaBase>
  );
}
