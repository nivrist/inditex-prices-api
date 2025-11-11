package com.inditex.prices.infrastructure.adapter.out.persistence.mapper;

import com.inditex.prices.domain.model.Price;
import com.inditex.prices.infrastructure.adapter.out.persistence.entity.PriceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper MapStruct para convertir entre Price (dominio) y PriceEntity (JPA).
 * Actúa como puente entre capas de dominio e infraestructura.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PriceEntityMapper {

    /**
     * Convierte entidad JPA a modelo de dominio.
     *
     * @param entity entidad JPA
     * @return modelo de dominio
     */
    Price toDomain(PriceEntity entity);

    /**
     * Convierte lista de entidades JPA a modelos de dominio.
     *
     * @param entities lista de entidades
     * @return lista de modelos de dominio
     */
    List<Price> toDomainList(List<PriceEntity> entities);

    /**
     * Convierte modelo de dominio a entidad JPA.
     *
     * @param domain modelo de dominio
     * @return entidad JPA
     */
    PriceEntity toEntity(Price domain);
}
