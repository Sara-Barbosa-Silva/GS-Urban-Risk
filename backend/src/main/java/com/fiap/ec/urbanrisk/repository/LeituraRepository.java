package com.fiap.ec.urbanrisk.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fiap.ec.urbanrisk.model.Leitura;

public interface LeituraRepository extends JpaRepository<Leitura, Long> {

    // Janela usada na média móvel do z-score
    List<Leitura> findTop20BySensorIdOrderByDataHoraDesc(Long sensorId);

    Optional<Leitura> findTopBySensorIdOrderByDataHoraDesc(Long sensorId);

    long countBySensorIdAndDataHoraAfter(Long sensorId, Instant desde);

    long countBySensorIdAndAnomaliaTrueAndDataHoraAfter(Long sensorId, Instant desde);

    Optional<Leitura> findTopByOrderByDataHoraDesc();

    long deleteByDataHoraBefore(Instant limite);
}
