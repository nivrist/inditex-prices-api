package com.inditex.prices.domain.model;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * Value Object inmutable que encapsula los parámetros de consulta de precio
 * (fecha, producto, marca) con validación de negocio.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@Value
@Builder
public class PriceQuery {

    LocalDateTime applicationDate;
    Long productId;
    Integer brandId;

    /**
     * Valida que los campos de la consulta cumplan las reglas de negocio
     * (no nulos, identificadores positivos).
     *
     * @throws IllegalArgumentException si los datos no son válidos
     */
    public void validate() {
        if (applicationDate == null) {
            throw new IllegalArgumentException("La fecha de aplicación es obligatoria");
        }

        if (productId == null) {
            throw new IllegalArgumentException("El identificador de producto es obligatorio");
        }

        if (productId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de producto debe ser positivo, recibido: " + productId
            );
        }

        if (brandId == null) {
            throw new IllegalArgumentException("El identificador de marca es obligatorio");
        }

        if (brandId <= 0) {
            throw new IllegalArgumentException(
                    "El identificador de marca debe ser positivo, recibido: " + brandId
            );
        }
    }
}
