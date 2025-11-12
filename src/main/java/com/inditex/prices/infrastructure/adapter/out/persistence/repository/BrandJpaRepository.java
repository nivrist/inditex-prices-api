package com.inditex.prices.infrastructure.adapter.out.persistence.repository;

import com.inditex.prices.infrastructure.adapter.out.persistence.entity.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la gestión de marcas/cadenas en la base de datos.
 */
@Repository
public interface BrandJpaRepository extends JpaRepository<BrandEntity, Integer> {
}
