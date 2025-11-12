package com.inditex.prices.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad Price.
 * Verifica lógica de aplicabilidad temporal y comparación de prioridades.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@DisplayName("Tests de la entidad Price")
class PriceTest {

    /**
     * Verifica que el precio es aplicable cuando la fecha está dentro del rango.
     */
    @Test
    @DisplayName("Debe ser aplicable cuando la fecha está dentro del rango de vigencia")
    void shouldBeApplicableWhenDateWithinRange() {
        // Given: Un precio con rango de vigencia definido
        LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        Price price = Price.builder()
                .id(1L)
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(startDate)
                .endDate(endDate)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        // When: Consultamos aplicabilidad con una fecha dentro del rango
        LocalDateTime applicationDate = LocalDateTime.of(2024, 6, 15, 10, 0);
        boolean isApplicable = price.isApplicableAt(applicationDate);

        // Then: El precio debe ser aplicable
        assertTrue(isApplicable,
                "El precio debería ser aplicable cuando la fecha está dentro del rango");
    }

    /**
     * Verifica que el precio no es aplicable antes del inicio de vigencia.
     */
    @Test
    @DisplayName("No debe ser aplicable cuando la fecha es anterior al inicio de vigencia")
    void shouldNotBeApplicableWhenDateBeforeStart() {
        // Given: Un precio con rango de vigencia definido
        LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        Price price = Price.builder()
                .id(1L)
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(startDate)
                .endDate(endDate)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        // When: Consultamos aplicabilidad con una fecha anterior al inicio
        LocalDateTime applicationDate = LocalDateTime.of(2024, 5, 31, 23, 59);
        boolean isApplicable = price.isApplicableAt(applicationDate);

        // Then: El precio NO debe ser aplicable
        assertFalse(isApplicable,
                "El precio no debería ser aplicable cuando la fecha es anterior al inicio");
    }

    /**
     * Verifica que el precio no es aplicable después del fin de vigencia.
     */
    @Test
    @DisplayName("No debe ser aplicable cuando la fecha es posterior al fin de vigencia")
    void shouldNotBeApplicableWhenDateAfterEnd() {
        // Given: Un precio con rango de vigencia definido
        LocalDateTime startDate = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        Price price = Price.builder()
                .id(1L)
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(startDate)
                .endDate(endDate)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        // When: Consultamos aplicabilidad con una fecha posterior al fin
        LocalDateTime applicationDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        boolean isApplicable = price.isApplicableAt(applicationDate);

        // Then: El precio NO debe ser aplicable
        assertFalse(isApplicable,
                "El precio no debería ser aplicable cuando la fecha es posterior al fin");
    }

    /**
     * Verifica que la comparación de prioridades funciona correctamente.
     */
    @Test
    @DisplayName("Debe comparar prioridades correctamente")
    void shouldComparePrioritiesCorrectly() {
        // Given: Dos precios con diferentes prioridades
        Price highPriorityPrice = Price.builder()
                .id(1L)
                .productId(35455L)
                .brandId(1)
                .priceList(2)
                .startDate(LocalDateTime.of(2024, 6, 14, 15, 0))
                .endDate(LocalDateTime.of(2024, 6, 14, 18, 30))
                .priority(1) // Mayor prioridad
                .price(new BigDecimal("25.45"))
                .currency("EUR")
                .build();

        Price lowPriorityPrice = Price.builder()
                .id(2L)
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(LocalDateTime.of(2024, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2024, 12, 31, 23, 59))
                .priority(0) // Menor prioridad
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        // When & Then: Verificamos las comparaciones de prioridad
        assertTrue(highPriorityPrice.hasHigherPriorityThan(lowPriorityPrice),
                "El precio con prioridad 1 debería tener mayor prioridad que el precio con prioridad 0");

        assertFalse(lowPriorityPrice.hasHigherPriorityThan(highPriorityPrice),
                "El precio con prioridad 0 no debería tener mayor prioridad que el precio con prioridad 1");
    }

    /**
     * Verifica que lanza NullPointerException con fecha null.
     */
    @Test
    @DisplayName("Debe lanzar NullPointerException cuando la fecha de aplicación es null")
    void shouldThrowNullPointerExceptionWhenApplicationDateIsNull() {
        // Given: Un precio válido
        Price price = Price.builder()
                .id(1L)
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(LocalDateTime.of(2024, 6, 1, 0, 0))
                .endDate(LocalDateTime.of(2024, 12, 31, 23, 59))
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        // When & Then: Intentamos verificar aplicabilidad con fecha null
        assertThrows(NullPointerException.class,
                () -> price.isApplicableAt(null),
                "Debería lanzar NullPointerException cuando la fecha es null");
    }

    /**
     * Verifica que lanza NullPointerException al comparar con precio null.
     */
    @Test
    @DisplayName("Debe lanzar NullPointerException cuando se compara con un precio null")
    void shouldThrowNullPointerExceptionWhenComparingWithNullPrice() {
        // Given: Un precio válido
        Price price = Price.builder()
                .id(1L)
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(LocalDateTime.of(2024, 6, 1, 0, 0))
                .endDate(LocalDateTime.of(2024, 12, 31, 23, 59))
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        // When & Then: Intentamos comparar con null
        assertThrows(NullPointerException.class,
                () -> price.hasHigherPriorityThan(null),
                "Debería lanzar NullPointerException cuando se compara con null");
    }

    /**
     * Verifica que el precio es aplicable en el límite de inicio.
     */
    @Test
    @DisplayName("Debe ser aplicable cuando la fecha es exactamente igual al inicio de vigencia")
    void shouldBeApplicableWhenDateEqualsStartDate() {
        // Given: Un precio con rango de vigencia definido
        LocalDateTime startDate = LocalDateTime.of(2024, 6, 14, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        Price price = Price.builder()
                .id(1L)
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(startDate)
                .endDate(endDate)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        // When: Consultamos aplicabilidad con fecha igual al inicio
        boolean isApplicable = price.isApplicableAt(startDate);

        // Then: El precio debe ser aplicable
        assertTrue(isApplicable,
                "El precio debería ser aplicable cuando la fecha es exactamente el inicio");
    }

    /**
     * Verifica que el precio es aplicable en el límite de fin.
     */
    @Test
    @DisplayName("Debe ser aplicable cuando la fecha es exactamente igual al fin de vigencia")
    void shouldBeApplicableWhenDateEqualsEndDate() {
        // Given: Un precio con rango de vigencia definido
        LocalDateTime startDate = LocalDateTime.of(2024, 6, 14, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);

        Price price = Price.builder()
                .id(1L)
                .productId(35455L)
                .brandId(1)
                .priceList(1)
                .startDate(startDate)
                .endDate(endDate)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .build();

        // When: Consultamos aplicabilidad con fecha igual al fin
        boolean isApplicable = price.isApplicableAt(endDate);

        // Then: El precio debe ser aplicable
        assertTrue(isApplicable,
                "El precio debería ser aplicable cuando la fecha es exactamente el fin");
    }
}
