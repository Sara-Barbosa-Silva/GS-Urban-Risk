import { StyleSheet } from "react-native";

import { cores, fonte } from "./tema";

export const styles = StyleSheet.create({
  conteudo: {
    padding: 16,
    paddingBottom: 40,
  },
  faixa: {
    height: 8,
    borderRadius: 4,
    marginBottom: 12,
  },
  titulo: {
    fontSize: fonte.titulo,
    fontWeight: "bold",
    color: cores.texto,
    marginTop: 12,
  },
  descricao: {
    fontSize: fonte.normal,
    color: cores.texto,
    marginTop: 10,
    lineHeight: 24,
  },
  secao: {
    backgroundColor: cores.superficie,
    borderRadius: 14,
    padding: 14,
    marginTop: 14,
  },
  secaoTitulo: {
    fontSize: fonte.normal,
    fontWeight: "bold",
    color: cores.primariaEscura,
    marginBottom: 6,
  },
  linha: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    marginTop: 4,
  },
  texto: {
    fontSize: fonte.pequena,
    color: cores.texto,
    flexShrink: 1,
  },
  barraFundo: {
    height: 14,
    borderRadius: 7,
    backgroundColor: cores.borda,
    marginTop: 8,
    overflow: "hidden",
  },
  barra: {
    height: 14,
    borderRadius: 7,
    backgroundColor: cores.primaria,
  },
  acoes: {
    marginTop: 20,
    gap: 12,
  },
  mensagem: {
    fontSize: fonte.pequena,
    marginTop: 12,
    fontWeight: "600",
  },
  vazio: {
    fontSize: fonte.normal,
    color: cores.textoSecundario,
    textAlign: "center",
    margin: 32,
  },
});
