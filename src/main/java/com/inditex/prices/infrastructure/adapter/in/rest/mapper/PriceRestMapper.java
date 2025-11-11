package com.inditex.prices.infrastructure.adapter.in.rest.mapper;

import com.inditex.prices.domain.model.Price;
import com.inditex.prices.infrastructure.adapter.in.rest.dto.PriceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper MapStruct para conversión entre entidades de dominio y DTOs REST.
 * Convierte objetos {@link Price} del dominio a {@link PriceResponse} para la capa REST.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PriceRestMapper {

    /**
     * Convierte un Price de dominio a PriceResponse DTO.
     *
     * @param price entidad de dominio
     * @return DTO de respuesta
     */
    PriceResponse toResponse(Price price);
}
