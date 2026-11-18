/**
 * BannerConexao - avisa quando o servidor cai e quando a conexão volta.
 * Fica fixo no topo de todas as telas.
 */
import React from "react";
import { View, Text } from "react-native";
import { Ionicons } from "@expo/vector-icons";

import { useAlertasTempoReal } from "../hooks/useAlertasTempoReal";
import { formatarHora } from "../utils/tempo";
import { bannerConexaoStyles as styles, cores } from "../styles";

export default function BannerConexao() {
  const { status, ultimaAtualizacao, mostrarReconectado, proximaTentativaMs } = useAlertasTempoReal();

  if (status === "offline" || status === "reconectando") {
    const desde = ultimaAtualizacao ? `Mostrando dados de ${formatarHora(ultimaAtualizacao)}. ` : "";
    const tentando =
      status === "reconectando"
        ? "Tentando reconectar agora..."
        : `Tentando reconectar${proximaTentativaMs ? ` em ${Math.round(proximaTentativaMs / 1000)}s` : ""}...`;
    return (
      <View
        style={[styles.banner, status === "offline" ? styles.offline : styles.reconectando]}
        accessibilityRole="alert"
        accessibilityLiveRegion="assertive"
        accessibilityLabel={`Sem conexão com o servidor. ${desde}${tentando}`}
      >
        <Ionicons name="cloud-offline" size={24} color={cores.branco} accessible={false} />
        <Text style={styles.texto}>
          Sem conexão com o servidor. {desde}
          {tentando}
        </Text>
      </View>
    );
  }

  if (mostrarReconectado) {
    return (
      <View
        style={[styles.banner, styles.online]}
        accessibilityRole="alert"
        accessibilityLiveRegion="polite"
        accessibilityLabel="Conexão restabelecida"
      >
        <Ionicons name="checkmark-circle" size={24} color={cores.branco} accessible={false} />
        <Text style={styles.texto}>Conexão restabelecida. Dados atualizados.</Text>
      </View>
    );
  }

  return null;
}
