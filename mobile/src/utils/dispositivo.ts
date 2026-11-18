import AsyncStorage from "@react-native-async-storage/async-storage";

const CHAVE = "urbanrisk:dispositivo";

/**
 * Identificador aleatório do aparelho, usado só para evitar confirmação
 * repetida do mesmo relato. Não identifica a pessoa (e o backend guarda só o hash).
 */
export async function idDispositivo(): Promise<string> {
  try {
    const salvo = await AsyncStorage.getItem(CHAVE);
    if (salvo) {
      return salvo;
    }
    const novo = `${Date.now().toString(36)}-${Math.random().toString(36).slice(2, 10)}`;
    await AsyncStorage.setItem(CHAVE, novo);
    return novo;
  } catch {
    return "desconhecido";
  }
}
