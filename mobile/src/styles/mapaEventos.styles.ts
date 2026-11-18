import { StyleSheet } from "react-native";

import { cores, fonte } from "./tema";

export const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  mapa: {
    flex: 1,
  },
  legenda: {
    flexDirection: "row",
    flexWrap: "wrap",
    backgroundColor: cores.superficie,
    paddingHorizontal: 12,
    paddingVertical: 10,
    gap: 12,
  },
  itemLegenda: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
  },
  bolinha: {
    width: 16,
    height: 16,
    borderRadius: 8,
  },
  textoLegenda: {
    fontSize: fonte.pequena,
    color: cores.texto,
  },
  aviso: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    backgroundColor: cores.primariaClara,
    margin: 12,
    padding: 14,
    borderRadius: 12,
  },
  avisoTexto: {
    flex: 1,
    fontSize: fonte.pequena,
    color: cores.primariaEscura,
  },
  grupo: {
    marginTop: 8,
  },
  grupoTitulo: {
    fontSize: fonte.media,
    fontWeight: "bold",
    color: cores.primariaEscura,
    marginHorizontal: 16,
    marginTop: 8,
    marginBottom: 4,
  },
  vazio: {
    fontSize: fonte.normal,
    color: cores.textoSecundario,
    textAlign: "center",
    margin: 24,
  },
});
