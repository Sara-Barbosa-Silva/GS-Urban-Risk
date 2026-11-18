import { StyleSheet } from "react-native";

import { fonte } from "./tema";

export const styles = StyleSheet.create({
  badge: {
    flexDirection: "row",
    alignItems: "center",
    alignSelf: "flex-start",
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 20,
    gap: 6,
  },
  texto: {
    fontSize: fonte.pequena,
    fontWeight: "bold",
  },
});
