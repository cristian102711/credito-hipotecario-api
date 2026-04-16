package com.creditohipotecario.api.repository;

import com.creditohipotecario.api.model.EstadoSolicitud;
import com.creditohipotecario.api.model.SolicitudCredito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SolicitudCreditoRepository extends JpaRepository<SolicitudCredito, Long> {

    Page<SolicitudCredito> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<SolicitudCredito> findByEstado(EstadoSolicitud estado, Pageable pageable);

    List<SolicitudCredito> findByUsuarioIdAndEstado(Long usuarioId, EstadoSolicitud estado);

    boolean existsByUsuarioIdAndPropiedadIdAndEstadoNotIn(
        Long usuarioId, Long propiedadId, List<EstadoSolicitud> estados);

    long countByEstado(EstadoSolicitud estado);

    @Query("SELECT COALESCE(SUM(s.montoSolicitado), 0) FROM SolicitudCredito s WHERE s.estado = :estado")
    BigDecimal sumMontoByEstado(EstadoSolicitud estado);

    @Query("SELECT COALESCE(SUM(s.montoSolicitado), 0) FROM SolicitudCredito s")
    BigDecimal sumMontoTotal();
}