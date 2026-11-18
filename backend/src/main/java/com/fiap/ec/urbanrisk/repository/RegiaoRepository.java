package com.fiap.ec.urbanrisk.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiap.ec.urbanrisk.model.Regiao;

public interface RegiaoRepository extends JpaRepository<Regiao, Long> {

    Optional<Regiao> findByNomeIgnoreCase(String nome);
}
