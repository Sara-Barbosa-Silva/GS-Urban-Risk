import { StyleSheet } from "react-native";

import { cores, fonte, ALVO_TOQUE } from "./tema";

export const styles = StyleSheet.create({
  banner: {
    flexDirection: "row",
    alignItems: "center",
    marginHorizontal: 12,
    marginTop: 8,
    padding: 12,
    borderRadius: 12,
    gap: 10,
    minHeight: ALVO_TOQUE,
  },
  conteudo: {
    flex: 1,
  },
  rotulo: {
    fontSize: fonte.pequena,
    fontWeight: "bold",
  },
  titulo: {
    fontSize: fonte.pequena,
  },
  fechar: {
    width: ALVO_TOQUE,
    height: ALVO_TOQUE,
    alignItems: "center",
    justifyContent: "center",
  },
  textoFechar: {
    color: cores.branco,
  },
});
