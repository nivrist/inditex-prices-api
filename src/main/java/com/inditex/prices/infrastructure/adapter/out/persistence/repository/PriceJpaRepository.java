package com.inditex.prices.infrastructure.adapter.out.persistence.repository;

import com.inditex.prices.infrastructure.adapter.out.persistence.entity.PriceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio Spring Data JPA para PriceEntity.
 * Proporciona operaciones CRUD y consultas personalizadas.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@Repository
public interface PriceJpaRepository extends JpaRepository<PriceEntity, Long> {

    /**
     * Busca precios aplicables para producto, marca y fecha dados.
     * Retorna resultados ordenados por prioridad descendente.
     *
     * @param productId identificador del producto
     * @param brandId identificador de la marca
     * @param applicationDate fecha de aplicación
     * @return lista de precios ordenados por prioridad
     */
    @Query("""
        SELECT p FROM PriceEntity p
        WHERE p.productId = :productId
        AND p.brandId = :brandId
        AND p.startDate <= :applicationDate
        AND p.endDate >= :applicationDate
        ORDER BY p.priority DESC
        """)
    List<PriceEntity> findApplicablePrices(
        @Param("productId") Long productId,
        @Param("brandId") Integer brandId,
        @Param("applicationDate") LocalDateTime applicationDate
    );
}
