import { StyleSheet } from "react-native";

import { cores, fonte } from "./tema";

export const styles = StyleSheet.create({
  filtros: {
    paddingHorizontal: 12,
    paddingVertical: 8,
  },
  rotuloFiltro: {
    fontSize: fonte.pequena,
    color: cores.textoSecundario,
    marginHorizontal: 12,
    marginTop: 6,
  },
  lista: {
    paddingBottom: 24,
  },
  resumo: {
    fontSize: fonte.pequena,
    color: cores.textoSecundario,
    marginHorizontal: 16,
    marginVertical: 6,
  },
  vazio: {
    fontSize: fonte.normal,
    color: cores.textoSecundario,
    textAlign: "center",
    margin: 32,
  },
});
