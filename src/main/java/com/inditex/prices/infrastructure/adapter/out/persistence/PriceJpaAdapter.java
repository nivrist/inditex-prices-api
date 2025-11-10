package com.inditex.prices.infrastructure.adapter.out.persistence;

import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.port.out.PriceRepository;
import com.inditex.prices.infrastructure.adapter.out.persistence.mapper.PriceEntityMapper;
import com.inditex.prices.infrastructure.adapter.out.persistence.repository.PriceJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Adaptador JPA que implementa el puerto PriceRepository.
 * Convierte entre entidades de persistencia y modelos de dominio.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PriceJpaAdapter implements PriceRepository {

    private final PriceJpaRepository jpaRepository;
    private final PriceEntityMapper mapper;

    /**
     * Busca precios en base de datos y los convierte a modelos de dominio.
     *
     * @param productId identificador del producto
     * @param brandId identificador de la marca
     * @param applicationDate fecha de aplicación
     * @return lista de precios de dominio
     */
    @Override
    public List<Price> findApplicablePrices(Long productId, Integer brandId, LocalDateTime applicationDate) {
        log.debug("Consultando BD para productId={}, brandId={}, fecha={}",
            productId, brandId, applicationDate);

        var entities = jpaRepository.findApplicablePrices(productId, brandId, applicationDate);

        log.debug("Base de datos retornó {} resultados", entities.size());

        return mapper.toDomainList(entities);
    }
}
