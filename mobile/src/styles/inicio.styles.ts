import { StyleSheet } from "react-native";

import { cores, fonte } from "./tema";

export const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: cores.primaria,
  },
  conteudo: {
    flexGrow: 1,
    justifyContent: "center",
    padding: 24,
  },
  icone: {
    alignSelf: "center",
    marginBottom: 12,
  },
  titulo: {
    fontSize: fonte.titulo + 4,
    fontWeight: "bold",
    color: cores.branco,
    textAlign: "center",
  },
  subtitulo: {
    fontSize: fonte.normal,
    color: "#D6ECEF",
    textAlign: "center",
    marginTop: 8,
    marginBottom: 32,
  },
  pergunta: {
    fontSize: fonte.grande,
    color: cores.branco,
    fontWeight: "600",
    textAlign: "center",
    marginBottom: 16,
  },
  opcao: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: cores.superficie,
    borderRadius: 16,
    padding: 18,
    marginBottom: 14,
    gap: 14,
    minHeight: 80,
  },
  opcaoTextos: {
    flex: 1,
  },
  opcaoTitulo: {
    fontSize: fonte.grande,
    fontWeight: "bold",
    color: cores.primariaEscura,
  },
  opcaoDescricao: {
    fontSize: fonte.pequena,
    color: cores.textoSecundario,
    marginTop: 2,
  },
  rodape: {
    fontSize: fonte.pequena,
    color: "#D6ECEF",
    textAlign: "center",
    marginTop: 20,
  },
});
