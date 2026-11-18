/**
 * Abas principais: Painel, Alertas, Mapa e Relatar
 */
import React from "react";
import { Pressable, Text } from "react-native";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { Ionicons } from "@expo/vector-icons";
import { useNavigation } from "@react-navigation/native";
import { NativeStackNavigationProp } from "@react-navigation/native-stack";

import { RootStackParamList, TabParamList } from "./types";
import PainelScreen from "../screens/PainelScreen";
import AlertasScreen from "../screens/AlertasScreen";
import MapaScreen from "../screens/MapaScreen";
import RelatarScreen from "../screens/RelatarScreen";
import { useAlertasTempoReal } from "../hooks/useAlertasTempoReal";
import { usePerfil } from "../context/PerfilContext";
import { cores, fonte } from "../styles";

const Tab = createBottomTabNavigator<TabParamList>();

const ICONES: Record<keyof TabParamList, keyof typeof Ionicons.glyphMap> = {
  Painel: "speedometer",
  Alertas: "notifications",
  Mapa: "map",
  Relatar: "megaphone",
};

function BotaoPerfil() {
  const navigation = useNavigation<NativeStackNavigationProp<RootStackParamList>>();
  const { perfil } = usePerfil();
  return (
    <Pressable
      onPress={() => navigation.replace("Inicio")}
      style={{ minHeight: 48, justifyContent: "center", paddingHorizontal: 14 }}
      accessibilityRole="button"
      accessibilityLabel={`Perfil atual: ${perfil === "agente" ? "agente local" : "morador"}. Toque para trocar.`}
    >
      <Text style={{ color: cores.branco, fontSize: fonte.pequena, fontWeight: "600" }}>
        {perfil === "agente" ? "Agente" : "Morador"} ▾
      </Text>
    </Pressable>
  );
}

export default function TabsNavigator() {
  const { alertas } = useAlertasTempoReal();
  const urgentes = alertas.filter((a) => a.nivelAlerta === "CRITICO" || a.nivelAlerta === "ALTO").length;

  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerStyle: { backgroundColor: cores.primaria },
        headerTintColor: cores.branco,
        headerTitleStyle: { fontWeight: "bold", fontSize: fonte.media },
        headerRight: () => <BotaoPerfil />,
        tabBarActiveTintColor: cores.primaria,
        tabBarInactiveTintColor: cores.textoSecundario,
        tabBarLabelStyle: { fontSize: 14, fontWeight: "600" },
        tabBarStyle: { minHeight: 64, paddingTop: 6 },
        tabBarIcon: ({ color, size }) => <Ionicons name={ICONES[route.name]} size={size + 2} color={color} />,
      })}
    >
      <Tab.Screen
        name="Painel"
        component={PainelScreen}
        options={{ title: "Painel", tabBarAccessibilityLabel: "Painel de acompanhamento" }}
      />
      <Tab.Screen
        name="Alertas"
        component={AlertasScreen}
        options={{
          title: "Alertas",
          tabBarBadge: urgentes > 0 ? urgentes : undefined,
          tabBarAccessibilityLabel: `Alertas, ${urgentes} urgentes`,
        }}
      />
      <Tab.Screen
        name="Mapa"
        component={MapaScreen}
        options={{ title: "Mapa", tabBarAccessibilityLabel: "Mapa de eventos" }}
      />
      <Tab.Screen
        name="Relatar"
        component={RelatarScreen}
        options={{ title: "Relatar", tabBarAccessibilityLabel: "Relatar um problema" }}
      />
    </Tab.Navigator>
  );
}
