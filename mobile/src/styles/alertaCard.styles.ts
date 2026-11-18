import { StyleSheet } from "react-native";

import { cores, fonte } from "./tema";

export const styles = StyleSheet.create({
  card: {
    backgroundColor: cores.superficie,
    borderRadius: 16,
    padding: 16,
    marginHorizontal: 12,
    marginVertical: 6,
    borderLeftWidth: 8,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.12,
    shadowRadius: 6,
    elevation: 3,
  },
  topo: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 10,
    gap: 8,
  },
  tempo: {
    fontSize: fonte.pequena,
    color: cores.textoSecundario,
  },
  titulo: {
    fontSize: fonte.media,
    fontWeight: "bold",
    color: cores.texto,
    marginBottom: 6,
  },
  linha: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    marginBottom: 4,
  },
  info: {
    fontSize: fonte.pequena,
    color: cores.textoSecundario,
    flexShrink: 1,
  },
  origem: {
    flexDirection: "row",
    alignItems: "center",
    alignSelf: "flex-start",
    backgroundColor: cores.primariaClara,
    borderRadius: 8,
    paddingHorizontal: 10,
    paddingVertical: 6,
    marginTop: 8,
    gap: 6,
  },
  origemTexto: {
    fontSize: fonte.pequena,
    color: cores.primariaEscura,
    fontWeight: "600",
  },
  motivo: {
    fontSize: fonte.pequena,
    color: cores.texto,
    marginTop: 8,
    fontStyle: "italic",
  },
});
