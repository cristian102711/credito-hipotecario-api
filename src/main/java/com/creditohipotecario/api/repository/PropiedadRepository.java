package com.creditohipotecario.api.repository;

import com.creditohipotecario.api.model.EstadoPropiedad;
import com.creditohipotecario.api.model.Propiedad;
import com.creditohipotecario.api.model.TipoPropiedad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {

    Page<Propiedad> findByEstado(EstadoPropiedad estado, Pageable pageable);

    Page<Propiedad> findByTipo(TipoPropiedad tipo, Pageable pageable);

    Page<Propiedad> findByComuna(String comuna, Pageable pageable);

    @Query("""
        SELECT p FROM Propiedad p
        WHERE (:estado IS NULL OR p.estado = :estado)
        AND (:tipo IS NULL OR p.tipo = :tipo)
        AND (:comuna IS NULL OR LOWER(p.comuna) = LOWER(:comuna))
        AND (:precioMin IS NULL OR p.precio >= :precioMin)
        AND (:precioMax IS NULL OR p.precio <= :precioMax)
        AND (:dormitorios IS NULL OR p.dormitorios >= :dormitorios)
    """)
    Page<Propiedad> filtrar(
        @Param("estado") EstadoPropiedad estado,
        @Param("tipo") TipoPropiedad tipo,
        @Param("comuna") String comuna,
        @Param("precioMin") BigDecimal precioMin,
        @Param("precioMax") BigDecimal precioMax,
        @Param("dormitorios") Integer dormitorios,
        Pageable pageable
    );
}