package com.inditex.prices.domain.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de dominio inmutable que representa un precio aplicable a un producto
 * con rango de vigencia y prioridad de aplicación.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@Value
@Builder
public class Price {

    Long id;
    Long productId;
    Integer brandId;
    Integer priceList;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Integer priority;
    BigDecimal price;
    String currency;

    /**
     * Verifica si el precio es aplicable en la fecha especificada (dentro del rango de vigencia).
     *
     * @param applicationDate fecha y hora a verificar
     * @return true si es aplicable, false en caso contrario
     * @throws NullPointerException si applicationDate es null
     */
    public boolean isApplicableAt(LocalDateTime applicationDate) {
        if (applicationDate == null) {
            throw new NullPointerException("La fecha de aplicación no puede ser null");
        }

        return !applicationDate.isBefore(startDate)
                && !applicationDate.isAfter(endDate);
    }

    /**
     * Compara la prioridad de este precio con otro.
     *
     * @param other precio a comparar
     * @return true si este precio tiene mayor prioridad
     * @throws NullPointerException si other es null
     */
    public boolean hasHigherPriorityThan(Price other) {
        if (other == null) {
            throw new NullPointerException("El precio a comparar no puede ser null");
        }

        return this.priority > other.priority;
    }
}
