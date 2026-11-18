import { StyleSheet } from "react-native";

import { cores, fonte } from "./tema";

export const styles = StyleSheet.create({
  banner: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 16,
    paddingVertical: 12,
    gap: 10,
  },
  offline: {
    backgroundColor: cores.offline,
  },
  reconectando: {
    backgroundColor: cores.reconectando,
  },
  online: {
    backgroundColor: cores.online,
  },
  texto: {
    flex: 1,
    color: cores.branco,
    fontSize: fonte.pequena,
    fontWeight: "600",
  },
});
