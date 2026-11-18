package com.fiap.ec.urbanrisk.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiap.ec.urbanrisk.model.RelatoComunitario;

public interface RelatoComunitarioRepository extends JpaRepository<RelatoComunitario, Long> {

    List<RelatoComunitario> findByRegiaoId(Long regiaoId);

    List<RelatoComunitario> findAllByOrderByCriadoEmDesc();

    // Retenção LGPD: relatos antigos que ainda guardam hash de contato
    List<RelatoComunitario> findByContatoHashIsNotNullAndCriadoEmBefore(Instant limite);
}
