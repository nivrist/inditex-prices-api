package com.inditex.prices.application.service;

import com.inditex.prices.domain.exception.InvalidQueryException;
import com.inditex.prices.domain.exception.PriceNotFoundException;
import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.model.PriceQuery;
import com.inditex.prices.domain.port.out.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PriceService.
 * Verifica selección por prioridad, validaciones y manejo de errores.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PriceService - Tests unitarios de la capa de aplicación")
class PriceServiceTest {

    @Mock
    private PriceRepository priceRepository;

    @InjectMocks
    private PriceService priceService;

    private static final Long PRODUCT_ID = 35455L;
    private static final Integer BRAND_ID = 1;
    private static final LocalDateTime TEST_DATE = LocalDateTime.of(2020, 6, 14, 16, 0);

    @BeforeEach
    void setUp() {
    }

    /**
     * Verifica que retorna el precio con mayor prioridad cuando múltiples precios se solapan.
     */
    @Test
    @DisplayName("Debe retornar el precio con mayor prioridad cuando múltiples precios se solapan")
    void shouldReturnHighestPriorityPriceWhenMultiplePricesOverlap() {
        // Arrange - Preparar datos de prueba
        Price lowPriority = Price.builder()
                .id(1L)
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(1)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .currency("EUR")
                .build();

        Price highPriority = Price.builder()
                .id(2L)
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(2)
                .priority(1)
                .price(new BigDecimal("25.45"))
                .startDate(LocalDateTime.of(2020, 6, 14, 15, 0))
                .endDate(LocalDateTime.of(2020, 6, 14, 18, 30))
                .currency("EUR")
                .build();

        PriceQuery query = PriceQuery.builder()
                .applicationDate(TEST_DATE)
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .build();

        when(priceRepository.findApplicablePrices(eq(PRODUCT_ID), eq(BRAND_ID), any()))
                .thenReturn(Arrays.asList(lowPriority, highPriority));

        // Act - Ejecutar el método bajo prueba
        Price result = priceService.getApplicablePrice(query);

        // Assert - Verificar resultados
        assertThat(result).isNotNull();
        assertThat(result.getPriceList()).isEqualTo(2);
        assertThat(result.getPrice()).isEqualByComparingTo("25.45");
        assertThat(result.getPriority()).isEqualTo(1);

        verify(priceRepository, times(1))
                .findApplicablePrices(PRODUCT_ID, BRAND_ID, TEST_DATE);
    }

    /**
     * Verifica que retorna correctamente cuando solo un precio coincide.
     */
    @Test
    @DisplayName("Debe retornar el precio único cuando solo un precio coincide")
    void shouldReturnSinglePriceWhenOnlyOneMatches() {
        // Arrange
        Price singlePrice = Price.builder()
                .id(1L)
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(1)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .currency("EUR")
                .build();

        PriceQuery query = PriceQuery.builder()
                .applicationDate(TEST_DATE)
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .build();

        when(priceRepository.findApplicablePrices(eq(PRODUCT_ID), eq(BRAND_ID), any()))
                .thenReturn(Collections.singletonList(singlePrice));

        // Act
        Price result = priceService.getApplicablePrice(query);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPrice()).isEqualByComparingTo("35.50");
        assertThat(result.getPriority()).isEqualTo(0);

        verify(priceRepository, times(1))
                .findApplicablePrices(PRODUCT_ID, BRAND_ID, TEST_DATE);
    }

    /**
     * Verifica que lanza PriceNotFoundException cuando no hay precios.
     */
    @Test
    @DisplayName("Debe lanzar PriceNotFoundException cuando no se encuentran precios")
    void shouldThrowPriceNotFoundExceptionWhenNoPricesFound() {
        // Arrange
        PriceQuery query = PriceQuery.builder()
                .applicationDate(TEST_DATE)
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .build();

        when(priceRepository.findApplicablePrices(eq(PRODUCT_ID), eq(BRAND_ID), any()))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        assertThatThrownBy(() -> priceService.getApplicablePrice(query))
                .isInstanceOf(PriceNotFoundException.class)
                .hasMessageContaining("No se encontró precio aplicable")
                .hasMessageContaining(PRODUCT_ID.toString())
                .hasMessageContaining(BRAND_ID.toString());

        verify(priceRepository, times(1))
                .findApplicablePrices(PRODUCT_ID, BRAND_ID, TEST_DATE);
    }

    /**
     * Verifica que lanza InvalidQueryException con parámetros inválidos.
     */
    @Test
    @DisplayName("Debe lanzar InvalidQueryException cuando la query es inválida")
    void shouldThrowInvalidQueryExceptionWhenQueryInvalid() {
        // Arrange - Query con productId negativo (inválido)
        PriceQuery invalidQuery = PriceQuery.builder()
                .applicationDate(TEST_DATE)
                .productId(-1L)
                .brandId(BRAND_ID)
                .build();

        // Act & Assert
        assertThatThrownBy(() -> priceService.getApplicablePrice(invalidQuery))
                .isInstanceOf(InvalidQueryException.class)
                .hasMessageContaining("positivo");

        // Verificar que el repositorio nunca fue llamado debido a la validación temprana
        verify(priceRepository, never())
                .findApplicablePrices(any(), any(), any());
    }

    /**
     * Verifica que filtra precios no aplicables en la fecha especificada.
     */
    @Test
    @DisplayName("Debe filtrar precios no aplicables en la fecha especificada")
    void shouldFilterOutPricesNotApplicableAtGivenDate() {
        // Arrange
        LocalDateTime queryDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        // Precio futuro - NO aplicable a las 10:00
        Price futurePrice = Price.builder()
                .id(1L)
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(2)
                .priority(1)
                .price(new BigDecimal("25.45"))
                .startDate(LocalDateTime.of(2020, 6, 14, 15, 0)) // Inicia a las 15:00
                .endDate(LocalDateTime.of(2020, 6, 14, 18, 30))
                .currency("EUR")
                .build();

        // Precio aplicable
        Price applicablePrice = Price.builder()
                .id(2L)
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(1)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .currency("EUR")
                .build();

        PriceQuery query = PriceQuery.builder()
                .applicationDate(queryDate)
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .build();

        when(priceRepository.findApplicablePrices(eq(PRODUCT_ID), eq(BRAND_ID), any()))
                .thenReturn(Arrays.asList(futurePrice, applicablePrice));

        // Act
        Price result = priceService.getApplicablePrice(query);

        // Assert - Debe retornar el precio aplicable, NO el futuro
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getPrice()).isEqualByComparingTo("35.50");
        assertThat(result.getPriority()).isEqualTo(0);
        assertThat(result.getPriceList()).isEqualTo(1);

        verify(priceRepository, times(1))
                .findApplicablePrices(PRODUCT_ID, BRAND_ID, queryDate);
    }

    /**
     * Verifica que lanza NullPointerException con query null.
     */
    @Test
    @DisplayName("Debe lanzar NullPointerException cuando la query es null")
    void shouldThrowNullPointerExceptionWhenQueryIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> priceService.getApplicablePrice(null))
                .isInstanceOf(NullPointerException.class);

        // El repositorio no debe ser invocado
        verify(priceRepository, never())
                .findApplicablePrices(any(), any(), any());
    }

    /**
     * Verifica que lanza InvalidQueryException con brandId cero.
     */
    @Test
    @DisplayName("Debe lanzar InvalidQueryException cuando brandId es cero")
    void shouldThrowInvalidQueryExceptionWhenBrandIdIsZero() {
        // Arrange
        PriceQuery invalidQuery = PriceQuery.builder()
                .applicationDate(TEST_DATE)
                .productId(PRODUCT_ID)
                .brandId(0)
                .build();

        // Act & Assert
        assertThatThrownBy(() -> priceService.getApplicablePrice(invalidQuery))
                .isInstanceOf(InvalidQueryException.class)
                .hasMessageContaining("positivo");

        verify(priceRepository, never())
                .findApplicablePrices(any(), any(), any());
    }

    /**
     * Verifica el manejo correcto de precios con la misma prioridad.
     */
    @Test
    @DisplayName("Debe manejar correctamente precios con la misma prioridad")
    void shouldHandlePricesWithSamePriority() {
        // Arrange
        Price firstPrice = Price.builder()
                .id(1L)
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(1)
                .priority(0)
                .price(new BigDecimal("35.50"))
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .currency("EUR")
                .build();

        Price secondPrice = Price.builder()
                .id(2L)
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .priceList(2)
                .priority(0) // Misma prioridad
                .price(new BigDecimal("36.00"))
                .startDate(LocalDateTime.of(2020, 6, 14, 0, 0))
                .endDate(LocalDateTime.of(2020, 12, 31, 23, 59))
                .currency("EUR")
                .build();

        PriceQuery query = PriceQuery.builder()
                .applicationDate(TEST_DATE)
                .productId(PRODUCT_ID)
                .brandId(BRAND_ID)
                .build();

        when(priceRepository.findApplicablePrices(eq(PRODUCT_ID), eq(BRAND_ID), any()))
                .thenReturn(Arrays.asList(firstPrice, secondPrice));

        // Act
        Price result = priceService.getApplicablePrice(query);

        // Assert - Debe retornar uno de los precios
        assertThat(result).isNotNull();
        assertThat(result.getPriority()).isEqualTo(0);
        assertThat(result.getProductId()).isEqualTo(PRODUCT_ID);

        verify(priceRepository, times(1))
                .findApplicablePrices(PRODUCT_ID, BRAND_ID, TEST_DATE);
    }
}
