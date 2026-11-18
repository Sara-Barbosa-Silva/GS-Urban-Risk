import { registerRootComponent } from "expo";

import App from "./App";

// registerRootComponent chama AppRegistry.registerComponent('main', () => App)
// e garante que o ambiente funcione no Expo Go e no build nativo
registerRootComponent(App);
