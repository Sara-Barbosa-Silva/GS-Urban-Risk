import { StyleSheet } from "react-native";

import { cores, fonte } from "./tema";

export const styles = StyleSheet.create({
  conteudo: {
    paddingVertical: 8,
    paddingBottom: 24,
  },
  atualizacao: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 6,
    paddingVertical: 8,
  },
  atualizacaoTexto: {
    fontSize: fonte.pequena,
    color: cores.textoSecundario,
  },
  totalLinha: {
    flexDirection: "row",
    alignItems: "baseline",
    gap: 8,
    marginBottom: 10,
  },
  totalNumero: {
    fontSize: 40,
    fontWeight: "bold",
    color: cores.primariaEscura,
  },
  totalTexto: {
    fontSize: fonte.normal,
    color: cores.texto,
  },
  linhaNivel: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingVertical: 8,
    borderTopWidth: 1,
    borderTopColor: cores.borda,
  },
  linhaRotulo: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
  },
  textoLinha: {
    fontSize: fonte.normal,
    color: cores.texto,
  },
  numeroLinha: {
    fontSize: fonte.grande,
    fontWeight: "bold",
    color: cores.texto,
    minWidth: 32,
    textAlign: "right",
  },
  regiaoNome: {
    fontSize: fonte.grande,
    fontWeight: "bold",
    color: cores.texto,
    marginBottom: 6,
  },
  textoInfo: {
    fontSize: fonte.pequena,
    color: cores.textoSecundario,
    marginTop: 6,
  },
  leitura: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    paddingVertical: 8,
    borderTopWidth: 1,
    borderTopColor: cores.borda,
    gap: 8,
  },
  leituraNome: {
    fontSize: fonte.pequena,
    color: cores.texto,
    flex: 1,
  },
  leituraValor: {
    fontSize: fonte.pequena,
    fontWeight: "bold",
    color: cores.texto,
  },
  leituraAlerta: {
    color: "#B71C1C",
  },
  carregando: {
    marginTop: 40,
  },
  erro: {
    fontSize: fonte.pequena,
    color: "#B71C1C",
    textAlign: "center",
    marginHorizontal: 16,
  },
});
