import { StyleSheet } from "react-native";

import { cores, fonte, ALVO_TOQUE } from "./tema";

export const styles = StyleSheet.create({
  conteudo: {
    padding: 16,
    paddingBottom: 40,
  },
  titulo: {
    fontSize: fonte.grande,
    fontWeight: "bold",
    color: cores.primariaEscura,
    marginBottom: 4,
  },
  explicacao: {
    fontSize: fonte.pequena,
    color: cores.textoSecundario,
    marginBottom: 16,
  },
  rotulo: {
    fontSize: fonte.normal,
    fontWeight: "bold",
    color: cores.texto,
    marginTop: 16,
    marginBottom: 8,
  },
  tipos: {
    gap: 10,
  },
  tipo: {
    flexDirection: "row",
    alignItems: "center",
    minHeight: 64,
    borderRadius: 14,
    borderWidth: 2,
    borderColor: cores.primaria,
    backgroundColor: cores.superficie,
    paddingHorizontal: 16,
    gap: 14,
  },
  tipoAtivo: {
    backgroundColor: cores.primaria,
  },
  tipoTexto: {
    fontSize: fonte.media,
    fontWeight: "600",
    color: cores.primaria,
  },
  tipoTextoAtivo: {
    color: cores.branco,
  },
  regioes: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
  },
  regiao: {
    minHeight: ALVO_TOQUE,
    paddingHorizontal: 14,
    justifyContent: "center",
    borderRadius: 24,
    borderWidth: 2,
    borderColor: cores.primaria,
    backgroundColor: cores.superficie,
  },
  regiaoAtiva: {
    backgroundColor: cores.primaria,
  },
  regiaoTexto: {
    fontSize: fonte.pequena,
    color: cores.primaria,
    fontWeight: "600",
  },
  regiaoTextoAtivo: {
    color: cores.branco,
  },
  input: {
    backgroundColor: cores.superficie,
    borderWidth: 1,
    borderColor: cores.borda,
    borderRadius: 12,
    padding: 12,
    fontSize: fonte.normal,
    color: cores.texto,
    minHeight: ALVO_TOQUE,
  },
  inputGrande: {
    minHeight: 96,
    textAlignVertical: "top",
  },
  privacidade: {
    flexDirection: "row",
    gap: 10,
    backgroundColor: cores.primariaClara,
    borderRadius: 12,
    padding: 12,
    marginTop: 8,
  },
  privacidadeTexto: {
    flex: 1,
    fontSize: fonte.pequena,
    color: cores.primariaEscura,
  },
  botao: {
    marginTop: 24,
  },
  erro: {
    fontSize: fonte.pequena,
    color: "#B71C1C",
    marginTop: 12,
    fontWeight: "600",
  },
  sucesso: {
    backgroundColor: "#E8F5E9",
    borderLeftWidth: 6,
    borderLeftColor: "#1B5E20",
    borderRadius: 12,
    padding: 14,
    marginTop: 16,
  },
  sucessoTexto: {
    fontSize: fonte.pequena,
    color: "#1B3A1D",
  },
});
