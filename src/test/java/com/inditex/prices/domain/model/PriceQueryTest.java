package com.inditex.prices.domain.model;

import com.inditex.prices.domain.exception.InvalidQueryException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para PriceQuery.
 * Verifica reglas de validación de consultas.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@DisplayName("Tests del Value Object PriceQuery")
class PriceQueryTest {

    /**
     * Verifica que se lanza InvalidQueryException cuando la fecha de aplicación es null.
     * <p>
     * Regla de negocio: La fecha de aplicación es obligatoria para cualquier consulta de precio.
     * </p>
     */
    @Test
    @DisplayName("Debe lanzar InvalidQueryException cuando la fecha de aplicación es null")
    void shouldThrowExceptionWhenApplicationDateNull() {
        // Given: Una consulta con fecha null pero otros campos válidos
        PriceQuery query = PriceQuery.builder()
                .applicationDate(null) // Fecha null (inválido)
                .productId(35455L)
                .brandId(1)
                .build();

        // When & Then: La validación debe lanzar excepción
        InvalidQueryException exception = assertThrows(
                InvalidQueryException.class,
                query::validate,
                "Debería lanzar InvalidQueryException cuando la fecha es null"
        );

        assertTrue(exception.getMessage().contains("fecha de aplicación"),
                "El mensaje de error debería mencionar 'fecha de aplicación'");
    }

    /**
     * Verifica que se lanza InvalidQueryException cuando el productId es null.
     * <p>
     * Regla de negocio: El identificador de producto es obligatorio para cualquier consulta.
     * </p>
     */
    @Test
    @DisplayName("Debe lanzar InvalidQueryException cuando el productId es null")
    void shouldThrowExceptionWhenProductIdNull() {
        // Given: Una consulta con productId null pero otros campos válidos
        PriceQuery query = PriceQuery.builder()
                .applicationDate(LocalDateTime.of(2024, 6, 14, 10, 0))
                .productId(null) // ProductId null (inválido)
                .brandId(1)
                .build();

        // When & Then: La validación debe lanzar excepción
        InvalidQueryException exception = assertThrows(
                InvalidQueryException.class,
                query::validate,
                "Debería lanzar InvalidQueryException cuando productId es null"
        );

        assertTrue(exception.getMessage().contains("producto"),
                "El mensaje de error debería mencionar 'producto'");
    }

    /**
     * Verifica que se lanza InvalidQueryException cuando el productId es negativo.
     * <p>
     * Regla de negocio: Los identificadores de producto deben ser valores positivos (mayor que 0).
     * </p>
     */
    @Test
    @DisplayName("Debe lanzar InvalidQueryException cuando el productId es negativo")
    void shouldThrowExceptionWhenProductIdNegative() {
        // Given: Una consulta con productId negativo
        PriceQuery query = PriceQuery.builder()
                .applicationDate(LocalDateTime.of(2024, 6, 14, 10, 0))
                .productId(-1L) // ProductId negativo (inválido)
                .brandId(1)
                .build();

        // When & Then: La validación debe lanzar excepción
        InvalidQueryException exception = assertThrows(
                InvalidQueryException.class,
                query::validate,
                "Debería lanzar InvalidQueryException cuando productId es negativo"
        );

        assertTrue(exception.getMessage().contains("positivo"),
                "El mensaje de error debería mencionar que debe ser 'positivo'");
        assertTrue(exception.getMessage().contains("-1"),
                "El mensaje de error debería incluir el valor recibido (-1)");
    }

    /**
     * Verifica que se lanza InvalidQueryException cuando el productId es cero.
     * <p>
     * Regla de negocio: Los identificadores de producto deben ser valores positivos (mayor que 0).
     * </p>
     */
    @Test
    @DisplayName("Debe lanzar InvalidQueryException cuando el productId es cero")
    void shouldThrowExceptionWhenProductIdZero() {
        // Given: Una consulta con productId cero
        PriceQuery query = PriceQuery.builder()
                .applicationDate(LocalDateTime.of(2024, 6, 14, 10, 0))
                .productId(0L) // ProductId cero (inválido)
                .brandId(1)
                .build();

        // When & Then: La validación debe lanzar excepción
        InvalidQueryException exception = assertThrows(
                InvalidQueryException.class,
                query::validate,
                "Debería lanzar InvalidQueryException cuando productId es cero"
        );

        assertTrue(exception.getMessage().contains("positivo"),
                "El mensaje de error debería mencionar que debe ser 'positivo'");
    }

    /**
     * Verifica que se lanza InvalidQueryException cuando el brandId es null.
     * <p>
     * Regla de negocio: El identificador de marca es obligatorio para cualquier consulta.
     * </p>
     */
    @Test
    @DisplayName("Debe lanzar InvalidQueryException cuando el brandId es null")
    void shouldThrowExceptionWhenBrandIdNull() {
        // Given: Una consulta con brandId null pero otros campos válidos
        PriceQuery query = PriceQuery.builder()
                .applicationDate(LocalDateTime.of(2024, 6, 14, 10, 0))
                .productId(35455L)
                .brandId(null) // BrandId null (inválido)
                .build();

        // When & Then: La validación debe lanzar excepción
        InvalidQueryException exception = assertThrows(
                InvalidQueryException.class,
                query::validate,
                "Debería lanzar InvalidQueryException cuando brandId es null"
        );

        assertTrue(exception.getMessage().contains("marca"),
                "El mensaje de error debería mencionar 'marca'");
    }

    /**
     * Verifica que se lanza InvalidQueryException cuando el brandId es negativo.
     * <p>
     * Regla de negocio: Los identificadores de marca deben ser valores positivos (mayor que 0).
     * </p>
     */
    @Test
    @DisplayName("Debe lanzar InvalidQueryException cuando el brandId es negativo")
    void shouldThrowExceptionWhenBrandIdNegative() {
        // Given: Una consulta con brandId negativo
        PriceQuery query = PriceQuery.builder()
                .applicationDate(LocalDateTime.of(2024, 6, 14, 10, 0))
                .productId(35455L)
                .brandId(-5) // BrandId negativo (inválido)
                .build();

        // When & Then: La validación debe lanzar excepción
        InvalidQueryException exception = assertThrows(
                InvalidQueryException.class,
                query::validate,
                "Debería lanzar InvalidQueryException cuando brandId es negativo"
        );

        assertTrue(exception.getMessage().contains("positivo"),
                "El mensaje de error debería mencionar que debe ser 'positivo'");
        assertTrue(exception.getMessage().contains("-5"),
                "El mensaje de error debería incluir el valor recibido (-5)");
    }

    /**
     * Verifica que se lanza InvalidQueryException cuando el brandId es cero.
     * <p>
     * Regla de negocio: Los identificadores de marca deben ser valores positivos (mayor que 0).
     * </p>
     */
    @Test
    @DisplayName("Debe lanzar InvalidQueryException cuando el brandId es cero")
    void shouldThrowExceptionWhenBrandIdZero() {
        // Given: Una consulta con brandId cero
        PriceQuery query = PriceQuery.builder()
                .applicationDate(LocalDateTime.of(2024, 6, 14, 10, 0))
                .productId(35455L)
                .brandId(0) // BrandId cero (inválido)
                .build();

        // When & Then: La validación debe lanzar excepción
        InvalidQueryException exception = assertThrows(
                InvalidQueryException.class,
                query::validate,
                "Debería lanzar InvalidQueryException cuando brandId es cero"
        );

        assertTrue(exception.getMessage().contains("positivo"),
                "El mensaje de error debería mencionar que debe ser 'positivo'");
    }

    /**
     * Verifica que NO se lanza excepción cuando la consulta contiene datos válidos.
     * <p>
     * Caso feliz: Todos los campos cumplen con las reglas de validación.
     * </p>
     */
    @Test
    @DisplayName("No debe lanzar excepción cuando la consulta es válida")
    void shouldNotThrowExceptionWhenValidQuery() {
        // Given: Una consulta completamente válida
        PriceQuery query = PriceQuery.builder()
                .applicationDate(LocalDateTime.of(2024, 6, 14, 10, 0))
                .productId(35455L)
                .brandId(1)
                .build();

        // When & Then: La validación debe completarse sin excepciones
        assertDoesNotThrow(
                query::validate,
                "No debería lanzar excepción cuando todos los campos son válidos"
        );
    }

    /**
     * Verifica el orden de validación: la fecha se valida primero.
     * <p>
     * Útil para asegurar que los mensajes de error sean consistentes y
     * que las validaciones fallen en orden lógico.
     * </p>
     */
    @Test
    @DisplayName("Debe validar la fecha antes que otros campos")
    void shouldValidateDateFirst() {
        // Given: Una consulta con múltiples campos inválidos, incluyendo fecha null
        PriceQuery query = PriceQuery.builder()
                .applicationDate(null) // Fecha null
                .productId(-1L) // ProductId también inválido
                .brandId(-1) // BrandId también inválido
                .build();

        // When & Then: La excepción debe ser por la fecha (se valida primero)
        InvalidQueryException exception = assertThrows(
                InvalidQueryException.class,
                query::validate
        );

        assertTrue(exception.getMessage().contains("fecha"),
                "La primera validación debería ser la fecha de aplicación");
    }
}
