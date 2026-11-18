package com.fiap.ec.urbanrisk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiap.ec.urbanrisk.model.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, Long> {

    Optional<Sensor> findByCodigoIgnoreCase(String codigo);

    List<Sensor> findByAtivoTrue();

    List<Sensor> findByRegiaoId(Long regiaoId);
}
