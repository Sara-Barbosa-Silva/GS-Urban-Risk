package com.fiap.ec.urbanrisk.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Proteção de dados pessoais (LGPD, Lei 13.709/2018).
 * Contatos e identificadores de aparelho viram hash SHA-256 com salt;
 * coordenadas são arredondadas antes de salvar.
 */
@Service
public class LgpdService {

    private final String salt;

    public LgpdService(@Value("${app.lgpd.salt}") String salt) {
        this.salt = salt;
    }

    public String hash(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String normalizado = valor.trim().toLowerCase(Locale.ROOT).replaceAll("[\\s()\\-.]", "");
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((salt + ":" + normalizado).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }

    /** 2 casas decimais: cerca de 1 km, suficiente para localizar o bairro. */
    public Double arredondar(Double coordenada) {
        if (coordenada == null) {
            return null;
        }
        return Math.round(coordenada * 100) / 100.0;
    }
}
