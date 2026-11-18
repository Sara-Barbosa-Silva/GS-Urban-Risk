package com.fiap.ec.urbanrisk.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fiap.ec.urbanrisk.dto.SensorDTO;
import com.fiap.ec.urbanrisk.repository.LeituraRepository;
import com.fiap.ec.urbanrisk.repository.SensorRepository;

@Service
public class SensorService {

    private final SensorRepository sensorRepository;
    private final LeituraRepository leituraRepository;
    private final MapeadorDTO mapeador;

    public SensorService(SensorRepository sensorRepository, LeituraRepository leituraRepository,
            MapeadorDTO mapeador) {
        this.sensorRepository = sensorRepository;
        this.leituraRepository = leituraRepository;
        this.mapeador = mapeador;
    }

    @Transactional(readOnly = true)
    public List<SensorDTO> listar(Long regiaoId) {
        var sensores = regiaoId == null ? sensorRepository.findAll() : sensorRepository.findByRegiaoId(regiaoId);
        return sensores.stream()
                .map(s -> mapeador.sensor(s, leituraRepository.findTopBySensorIdOrderByDataHoraDesc(s.getId())
                        .orElse(null)))
                .toList();
    }
}
