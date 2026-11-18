import { StyleSheet } from "react-native";

import { cores, fonte } from "./tema";

export const styles = StyleSheet.create({
  card: {
    backgroundColor: cores.superficie,
    borderRadius: 16,
    padding: 16,
    marginHorizontal: 12,
    marginVertical: 6,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 6,
    elevation: 2,
  },
  cabecalho: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    marginBottom: 10,
  },
  titulo: {
    fontSize: fonte.media,
    fontWeight: "bold",
    color: cores.primariaEscura,
  },
});
