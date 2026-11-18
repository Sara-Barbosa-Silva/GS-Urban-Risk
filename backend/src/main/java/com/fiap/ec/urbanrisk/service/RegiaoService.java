package com.fiap.ec.urbanrisk.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fiap.ec.urbanrisk.dto.RegiaoDTO;
import com.fiap.ec.urbanrisk.model.StatusEvento;
import com.fiap.ec.urbanrisk.repository.EventoRepository;
import com.fiap.ec.urbanrisk.repository.RegiaoRepository;

@Service
public class RegiaoService {

    private final RegiaoRepository regiaoRepository;
    private final EventoRepository eventoRepository;
    private final MapeadorDTO mapeador;

    public RegiaoService(RegiaoRepository regiaoRepository, EventoRepository eventoRepository, MapeadorDTO mapeador) {
        this.regiaoRepository = regiaoRepository;
        this.eventoRepository = eventoRepository;
        this.mapeador = mapeador;
    }

    @Transactional(readOnly = true)
    public List<RegiaoDTO> listar() {
        return regiaoRepository.findAll().stream()
                .map(r -> mapeador.regiao(r, eventoRepository.findByRegiaoId(r.getId()).stream()
                        .filter(e -> e.getStatus() == StatusEvento.ATIVO).count()))
                .toList();
    }
}
