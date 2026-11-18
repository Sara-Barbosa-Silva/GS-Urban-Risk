package com.fiap.ec.urbanrisk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LgpdServiceTest {

    private final LgpdService lgpd = new LgpdService("salt-de-teste");

    @Test
    void hashNaoContemOContatoOriginal() {
        String hash = lgpd.hash("(11) 98888-7777");
        assertEquals(64, hash.length());
        assertFalse(hash.contains("98888"));
    }

    @Test
    void mesmoContatoEmFormatosDiferentesGeraOMesmoHash() {
        assertEquals(lgpd.hash("(11) 98888-7777"), lgpd.hash("11988887777"));
        assertEquals(lgpd.hash("Maria@Email.com "), lgpd.hash("maria@email.com"));
    }

    @Test
    void saltDiferenteGeraHashDiferente() {
        assertNotEquals(lgpd.hash("11988887777"), new LgpdService("outro-salt").hash("11988887777"));
    }

    @Test
    void contatoVazioNaoGeraHash() {
        assertNull(lgpd.hash(null));
        assertNull(lgpd.hash("   "));
    }

    @Test
    void coordenadaArredondadaParaNivelDeBairro() {
        assertEquals(-23.56, lgpd.arredondar(-23.561234));
        assertEquals(-46.6, lgpd.arredondar(-46.598765));
    }
}
