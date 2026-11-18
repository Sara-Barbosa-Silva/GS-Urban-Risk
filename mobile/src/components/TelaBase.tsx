/**
 * TelaBase - envolve as telas principais com os avisos globais:
 * banner de conexão e banner de novo alerta.
 */
import React from "react";
import { View } from "react-native";
import { useNavigation } from "@react-navigation/native";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";

import BannerConexao from "./BannerConexao";
import BannerNovoAlerta from "./BannerNovoAlerta";
import { useAlertasTempoReal } from "../hooks/useAlertasTempoReal";
import { RootStackParamList } from "../navigation/types";
import { telaBaseStyles as styles } from "../styles";

export default function TelaBase({ children }: { children: React.ReactNode }) {
  const { alertaDestaque, dispensarDestaque } = useAlertasTempoReal();
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();

  return (
    <View style={styles.container}>
      <BannerConexao />
      <BannerNovoAlerta
        alerta={alertaDestaque}
        onFechar={dispensarDestaque}
        onAbrir={(alerta) => {
          dispensarDestaque();
          navigation.navigate("DetalheAlerta", { alertaId: alerta.id });
        }}
      />
      {children}
    </View>
  );
}
