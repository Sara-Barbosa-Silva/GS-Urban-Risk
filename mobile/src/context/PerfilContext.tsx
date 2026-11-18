/**
 * PerfilContext - morador ou agente local (sem login; só muda o que aparece)
 */
import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";

import { Perfil } from "../types/perfil";

const CHAVE = "urbanrisk:perfil";

type PerfilContextValor = {
  perfil: Perfil | null;
  escolherPerfil: (perfil: Perfil) => void;
};

const PerfilContext = createContext<PerfilContextValor>({ perfil: null, escolherPerfil: () => {} });

export function PerfilProvider({ children }: { children: React.ReactNode }) {
  const [perfil, setPerfil] = useState<Perfil | null>(null);

  useEffect(() => {
    AsyncStorage.getItem(CHAVE)
      .then((salvo) => {
        if (salvo === "morador" || salvo === "agente") setPerfil(salvo);
      })
      .catch(() => {});
  }, []);

  const valor = useMemo(
    () => ({
      perfil,
      escolherPerfil: (novo: Perfil) => {
        setPerfil(novo);
        AsyncStorage.setItem(CHAVE, novo).catch(() => {});
      },
    }),
    [perfil]
  );

  return <PerfilContext.Provider value={valor}>{children}</PerfilContext.Provider>;
}

export function usePerfil() {
  return useContext(PerfilContext);
}
