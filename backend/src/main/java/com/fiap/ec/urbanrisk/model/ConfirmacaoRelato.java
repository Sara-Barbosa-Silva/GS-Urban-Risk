package com.fiap.ec.urbanrisk.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Registro de que um aparelho confirmou um relato.
 * Evita que o mesmo aparelho infle a confiança confirmando várias vezes.
 * O identificador do aparelho também é guardado só como hash.
 */
@Entity
@Table(name = "confirmacoes_relato", uniqueConstraints = {
        @UniqueConstraint(name = "uk_confirmacao_relato_dispositivo", columnNames = { "relatoId", "dispositivoHash" })
})
public class ConfirmacaoRelato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long relatoId;

    @Column(nullable = false, length = 64)
    private String dispositivoHash;

    @Column(nullable = false)
    private Instant criadoEm;

    public ConfirmacaoRelato() {
    }

    public ConfirmacaoRelato(Long relatoId, String dispositivoHash, Instant criadoEm) {
        this.relatoId = relatoId;
        this.dispositivoHash = dispositivoHash;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public Long getRelatoId() {
        return relatoId;
    }

    public String getDispositivoHash() {
        return dispositivoHash;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
