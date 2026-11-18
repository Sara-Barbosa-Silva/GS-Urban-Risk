import { StyleSheet } from "react-native";

import { cores, fonte, ALVO_TOQUE } from "./tema";

export const styles = StyleSheet.create({
  botao: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    minHeight: ALVO_TOQUE + 8,
    borderRadius: 14,
    paddingHorizontal: 18,
    backgroundColor: cores.primaria,
    gap: 10,
  },
  secundario: {
    backgroundColor: cores.superficie,
    borderWidth: 2,
    borderColor: cores.primaria,
  },
  desabilitado: {
    opacity: 0.5,
  },
  texto: {
    color: cores.branco,
    fontSize: fonte.normal,
    fontWeight: "bold",
  },
  textoSecundario: {
    color: cores.primaria,
  },
});
