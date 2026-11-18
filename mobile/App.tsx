/**
 * App.tsx - Urban Risk Intelligence Platform
 *
 * Monitoramento de riscos urbanos (alagamentos, ilhas de calor e interrupção de serviços)
 * para moradores e agentes locais. Consome a API Spring Boot em tempo real (polling de 5 s),
 * mantém o último dado em cache e se reconecta sozinho quando o servidor volta.
 */

import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { SafeAreaProvider } from "react-native-safe-area-context";
import { StatusBar } from "expo-status-bar";

import { RootStackParamList } from "./src/navigation/types";
import TabsNavigator from "./src/navigation/TabsNavigator";
import { ConexaoProvider } from "./src/context/ConexaoContext";
import { PerfilProvider } from "./src/context/PerfilContext";
import InicioScreen from "./src/screens/InicioScreen";
import DetalheAlertaScreen from "./src/screens/DetalheAlertaScreen";
import { cores } from "./src/styles";

const Stack = createNativeStackNavigator<RootStackParamList>();

export default function App() {
  return (
    <SafeAreaProvider>
      <PerfilProvider>
        <ConexaoProvider>
          <NavigationContainer>
            <StatusBar style="light" />
            <Stack.Navigator
              initialRouteName="Inicio"
              screenOptions={{
                headerStyle: { backgroundColor: cores.primaria },
                headerTintColor: cores.branco,
                headerTitleStyle: { fontWeight: "bold" },
              }}
            >
              {/* Escolha do perfil - sem cabeçalho */}
              <Stack.Screen name="Inicio" component={InicioScreen} options={{ headerShown: false }} />

              {/* Abas principais */}
              <Stack.Screen name="Principal" component={TabsNavigator} options={{ headerShown: false }} />

              <Stack.Screen
                name="DetalheAlerta"
                component={DetalheAlertaScreen}
                options={{ title: "Detalhe do alerta" }}
              />
            </Stack.Navigator>
          </NavigationContainer>
        </ConexaoProvider>
      </PerfilProvider>
    </SafeAreaProvider>
  );
}
