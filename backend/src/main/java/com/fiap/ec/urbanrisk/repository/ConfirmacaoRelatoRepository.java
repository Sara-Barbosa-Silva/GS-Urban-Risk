package com.fiap.ec.urbanrisk.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiap.ec.urbanrisk.model.ConfirmacaoRelato;

public interface ConfirmacaoRelatoRepository extends JpaRepository<ConfirmacaoRelato, Long> {

    boolean existsByRelatoIdAndDispositivoHash(Long relatoId, String dispositivoHash);
}
