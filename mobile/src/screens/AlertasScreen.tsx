/**
 * AlertasScreen - lista ordenada pela prioridade da API, com filtros por tipo e nível
 */
import React, { useMemo, useState } from "react";
import { FlatList, ScrollView, Text, View, RefreshControl } from "react-native";
import { useNavigation } from "@react-navigation/native";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";

import { TelaBase, AlertaCard, ChipFiltro } from "../components";
import { useAlertasTempoReal } from "../hooks/useAlertasTempoReal";
import { useAgora } from "../hooks/useAgora";
import { RootStackParamList } from "../navigation/types";
import { NivelAlerta } from "../types/nivelAlerta";
import { TipoRisco } from "../types/tipoRisco";
import { infoNivel, NIVEIS_ORDENADOS } from "../utils/nivel";
import { infoTipo, TIPOS_RISCO } from "../utils/tipoRisco";
import { alertasStyles as styles } from "../styles";

export default function AlertasScreen() {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const { alertas, atualizarAgora } = useAlertasTempoReal();
  const agora = useAgora(15000);
  const [tipo, setTipo] = useState<TipoRisco | null>(null);
  const [nivel, setNivel] = useState<NivelAlerta | null>(null);
  const [atualizando, setAtualizando] = useState(false);

  const filtrados = useMemo(
    () => alertas.filter((a) => (!tipo || a.tipoRisco === tipo) && (!nivel || a.nivelAlerta === nivel)),
    [alertas, tipo, nivel]
  );

  async function puxarParaAtualizar() {
    setAtualizando(true);
    await atualizarAgora();
    setAtualizando(false);
  }

  return (
    <TelaBase>
      <FlatList
        data={filtrados}
        keyExtractor={(item) => String(item.id)}
        contentContainerStyle={styles.lista}
        refreshControl={<RefreshControl refreshing={atualizando} onRefresh={puxarParaAtualizar} />}
        ListHeaderComponent={
          <View>
            <Text style={styles.rotuloFiltro}>Tipo de risco</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.filtros}>
              <ChipFiltro texto="Todos" ativo={tipo === null} onPress={() => setTipo(null)} />
              {TIPOS_RISCO.map((t) => (
                <ChipFiltro
                  key={t}
                  texto={infoTipo(t).texto}
                  icone={infoTipo(t).icone}
                  ativo={tipo === t}
                  onPress={() => setTipo(tipo === t ? null : t)}
                />
              ))}
            </ScrollView>
            <Text style={styles.rotuloFiltro}>Nível</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.filtros}>
              <ChipFiltro texto="Todos" ativo={nivel === null} onPress={() => setNivel(null)} />
              {NIVEIS_ORDENADOS.map((n) => (
                <ChipFiltro
                  key={n}
                  texto={infoNivel(n).texto}
                  icone={infoNivel(n).icone}
                  corIcone={infoNivel(n).cor}
                  ativo={nivel === n}
                  onPress={() => setNivel(nivel === n ? null : n)}
                />
              ))}
            </ScrollView>
            <Text style={styles.resumo} accessibilityLiveRegion="polite">
              {filtrados.length} {filtrados.length === 1 ? "alerta" : "alertas"} · mais urgentes primeiro
            </Text>
          </View>
        }
        ListEmptyComponent={<Text style={styles.vazio}>Nenhum alerta com esses filtros. Tudo tranquilo por aqui.</Text>}
        renderItem={({ item }) => (
          <AlertaCard
            alerta={item}
            agora={agora}
            onPress={() => navigation.navigate("DetalheAlerta", { alertaId: item.id })}
          />
        )}
      />
    </TelaBase>
  );
}
