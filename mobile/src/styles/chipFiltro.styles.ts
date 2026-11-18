import { StyleSheet } from "react-native";

import { cores, fonte, ALVO_TOQUE } from "./tema";

export const styles = StyleSheet.create({
  chip: {
    flexDirection: "row",
    alignItems: "center",
    minHeight: ALVO_TOQUE,
    paddingHorizontal: 14,
    borderRadius: 24,
    borderWidth: 2,
    borderColor: cores.primaria,
    backgroundColor: cores.superficie,
    marginRight: 8,
    gap: 6,
  },
  ativo: {
    backgroundColor: cores.primaria,
  },
  texto: {
    fontSize: fonte.pequena,
    color: cores.primaria,
    fontWeight: "600",
  },
  textoAtivo: {
    color: cores.branco,
  },
});
