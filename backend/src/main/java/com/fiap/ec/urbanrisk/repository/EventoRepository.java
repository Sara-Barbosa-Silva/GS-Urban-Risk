package com.fiap.ec.urbanrisk.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fiap.ec.urbanrisk.model.Evento;
import com.fiap.ec.urbanrisk.model.Origem;
import com.fiap.ec.urbanrisk.model.StatusEvento;
import com.fiap.ec.urbanrisk.model.TipoRisco;

public interface EventoRepository extends JpaRepository<Evento, Long>, JpaSpecificationExecutor<Evento> {

    List<Evento> findByStatusOrderByScorePrioridadeDesc(StatusEvento status);

    List<Evento> findByRegiaoId(Long regiaoId);

    List<Evento> findByAtualizadoEmAfter(Instant desde);

    Optional<Evento> findFirstBySensorIdAndStatus(Long sensorId, StatusEvento status);

    Optional<Evento> findFirstByCodigoExterno(String codigoExterno);

    Optional<Evento> findFirstByRelatoId(Long relatoId);

    List<Evento> findByStatusAndAtualizadoEmBefore(StatusEvento status, Instant limite);

    boolean existsByRegiaoIdAndTipoRiscoAndOrigemAndStatus(Long regiaoId, TipoRisco tipoRisco,
            Origem origem, StatusEvento status);

    // Corroboração: origens diferentes apontando o mesmo risco na mesma região
    @Query("""
            select distinct e.origem from Evento e
            where e.regiao.id = :regiaoId and e.tipoRisco = :tipo
              and e.status = com.fiap.ec.urbanrisk.model.StatusEvento.ATIVO
              and e.atualizadoEm >= :desde
            """)
    List<Origem> origensAtivas(@Param("regiaoId") Long regiaoId, @Param("tipo") TipoRisco tipo,
            @Param("desde") Instant desde);

    long countByStatus(StatusEvento status);

    List<Evento> findByStatusIn(Collection<StatusEvento> status);
}
