import AsyncStorage from "@react-native-async-storage/async-storage";
import * as Notifications from "expo-notifications";
import { Platform } from "react-native";

import { Alerta } from "../interfaces/alerta";
import { deveNotificar, infoNivel } from "../utils/nivel";

const CHAVE_NOTIFICADOS = "urbanrisk:notificados";
const LIMITE_IDS = 500;

let permissaoPedida = false;
let notificados: Set<number> | null = null;

/**
 * Notificação LOCAL (funciona no Expo Go). Push remoto não é usado.
 * No Expo Web o aviso aparece só como banner dentro do app.
 */
export async function prepararNotificacoes(): Promise<boolean> {
  if (Platform.OS === "web") {
    return false;
  }
  try {
    Notifications.setNotificationHandler({
      handleNotification: async () => ({
        shouldShowBanner: true,
        shouldShowList: true,
        shouldPlaySound: true,
        shouldSetBadge: false,
      }),
    });
    if (Platform.OS === "android") {
      await Notifications.setNotificationChannelAsync("alertas", {
        name: "Alertas de risco",
        importance: Notifications.AndroidImportance.HIGH,
      });
    }
    if (!permissaoPedida) {
      permissaoPedida = true;
      const atual = await Notifications.getPermissionsAsync();
      if (atual.status !== "granted") {
        const pedido = await Notifications.requestPermissionsAsync();
        return pedido.status === "granted";
      }
    }
    return true;
  } catch {
    return false;
  }
}

async function carregarNotificados(): Promise<Set<number> | null> {
  if (notificados) {
    return notificados;
  }
  try {
    const texto = await AsyncStorage.getItem(CHAVE_NOTIFICADOS);
    notificados = texto ? new Set<number>(JSON.parse(texto)) : null;
  } catch {
    notificados = null;
  }
  return notificados;
}

async function salvarNotificados(ids: Set<number>): Promise<void> {
  const lista = Array.from(ids).slice(-LIMITE_IDS);
  notificados = new Set(lista);
  try {
    await AsyncStorage.setItem(CHAVE_NOTIFICADOS, JSON.stringify(lista));
  } catch {
    // ignora: no pior caso o aviso se repete depois de reiniciar o app
  }
}

/**
 * Notifica alertas ALTO ou CRÍTICO que ainda não foram avisados.
 * Cada alerta é notificado uma única vez (ids guardados no aparelho).
 * Na primeira vez que o app roda, só registra os alertas existentes,
 * para não disparar uma enxurrada de avisos antigos.
 * Devolve os alertas novos (usados no banner dentro do app).
 */
export async function notificarNovos(alertas: Alerta[]): Promise<Alerta[]> {
  const relevantes = alertas.filter((a) => a.status === "ATIVO" && deveNotificar(a.nivelAlerta));
  const jaAvisados = await carregarNotificados();

  if (jaAvisados === null) {
    await salvarNotificados(new Set(relevantes.map((a) => a.id)));
    return [];
  }

  const novos = relevantes.filter((a) => !jaAvisados.has(a.id));
  if (novos.length === 0) {
    return [];
  }
  novos.forEach((a) => jaAvisados.add(a.id));
  await salvarNotificados(jaAvisados);

  if (Platform.OS !== "web") {
    for (const alerta of novos.slice(0, 3)) {
      try {
        await Notifications.scheduleNotificationAsync({
          content: {
            title: `${infoNivel(alerta.nivelAlerta).texto}: ${alerta.regiaoNome}`,
            body: alerta.titulo,
            data: { alertaId: alerta.id },
          },
          trigger: null,
        });
      } catch {
        // sem permissão: o banner dentro do app continua avisando
      }
    }
  }
  return novos;
}

// O próprio aparelho que enviou o relato não precisa ser avisado dele
export async function marcarComoNotificado(alertaId: number): Promise<void> {
  const ids = (await carregarNotificados()) ?? new Set<number>();
  ids.add(alertaId);
  await salvarNotificados(ids);
}

// Usado nos testes
export function _resetarNotificados() {
  notificados = null;
  permissaoPedida = false;
}
