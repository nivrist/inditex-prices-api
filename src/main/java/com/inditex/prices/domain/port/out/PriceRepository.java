package com.inditex.prices.domain.port.out;

import com.inditex.prices.domain.model.Price;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Puerto de salida para acceso a datos de precios.
 * Define el contrato de persistencia independiente de la tecnología específica.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
public interface PriceRepository {

    /**
     * Busca precios candidatos para un producto, marca y fecha específicos.
     *
     * @param productId identificador del producto
     * @param brandId identificador de la marca
     * @param applicationDate fecha de aplicación
     * @return lista de precios encontrados, vacía si no hay coincidencias
     */
    List<Price> findApplicablePrices(Long productId, Integer brandId, LocalDateTime applicationDate);
}
