// Mocks usados nos testes
jest.mock("@react-native-async-storage/async-storage", () =>
  require("@react-native-async-storage/async-storage/jest/async-storage-mock")
);

jest.mock("expo-notifications", () => ({
  setNotificationHandler: jest.fn(),
  getPermissionsAsync: jest.fn(async () => ({ status: "granted" })),
  requestPermissionsAsync: jest.fn(async () => ({ status: "granted" })),
  scheduleNotificationAsync: jest.fn(async () => "id"),
  setNotificationChannelAsync: jest.fn(async () => null),
  AndroidImportance: { HIGH: 4 },
}));

// Ícones carregam fonte de forma assíncrona; nos testes basta um componente vazio
jest.mock("@expo/vector-icons", () => {
  const Icone = () => null;
  Icone.glyphMap = {};
  return { Ionicons: Icone };
});
