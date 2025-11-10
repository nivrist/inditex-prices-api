package com.inditex.prices.infrastructure.adapter.out.persistence;

import com.inditex.prices.domain.model.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración para PriceJpaAdapter.
 * Verifica la correcta integración entre adaptador, repositorio JPA, entidades, mapper y H2.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = "com.inditex.prices.infrastructure.adapter.out.persistence")
@DisplayName("PriceJpaAdapter Integration Tests")
class PriceJpaAdapterIntegrationTest {

    @Autowired
    private PriceJpaAdapter priceJpaAdapter;

    /**
     * Test 1: Petición a las 10:00 del día 14 del producto 35455 para la brand 1 (ZARA)
     * <p>
     * Caso esperado: En este momento solo aplica el precio base (PRICE_LIST=1).
     * El precio promocional del mediodía (PRICE_LIST=2) aún no ha comenzado.
     * </p>
     * <p>
     * Validaciones:
     * <ul>
     *   <li>Se encuentra exactamente 1 precio</li>
     *   <li>El precio corresponde a la lista de precios 1 (base)</li>
     *   <li>El precio es 35.50 EUR</li>
     *   <li>La prioridad es 0</li>
     * </ul>
     * </p>
     */
    @Test
    @DisplayName("Debe encontrar precios aplicables desde la BD para el Test 1 (10:00 día 14)")
    void shouldFindApplicablePricesTest1() {
        // Arrange: Preparar datos de entrada
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        // Act: Ejecutar la consulta
        List<Price> prices = priceJpaAdapter.findApplicablePrices(35455L, 1, applicationDate);

        // Assert: Verificar resultados
        assertThat(prices).isNotEmpty();
        assertThat(prices).hasSize(1);

        Price price = prices.get(0);
        assertThat(price.getPriceList()).isEqualTo(1);
        assertThat(price.getPrice()).isEqualByComparingTo(new BigDecimal("35.50"));
        assertThat(price.getCurrency()).isEqualTo("EUR");
        assertThat(price.getPriority()).isEqualTo(0);
        assertThat(price.getProductId()).isEqualTo(35455L);
        assertThat(price.getBrandId()).isEqualTo(1);
    }

    /**
     * Test 2: Petición a las 16:00 del día 14 del producto 35455 para la brand 1 (ZARA)
     * <p>
     * Caso esperado: En este momento aplican 2 precios:
     * <ul>
     *   <li>Precio base (PRICE_LIST=1, PRIORITY=0) - Todo el periodo</li>
     *   <li>Precio promocional (PRICE_LIST=2, PRIORITY=1) - 15:00 a 18:30</li>
     * </ul>
     * El precio con mayor prioridad (PRICE_LIST=2) debe aparecer primero.
     * </p>
     * <p>
     * Validaciones:
     * <ul>
     *   <li>Se encuentran exactamente 2 precios</li>
     *   <li>El primero tiene prioridad 1 (precio promocional)</li>
     *   <li>El primero tiene PRICE_LIST=2 y precio 25.45 EUR</li>
     *   <li>El segundo tiene prioridad 0 (precio base)</li>
     *   <li>Los precios están ordenados por prioridad descendente</li>
     * </ul>
     * </p>
     */
    @Test
    @DisplayName("Debe encontrar precios aplicables desde la BD para el Test 2 (16:00 día 14)")
    void shouldFindApplicablePricesTest2() {
        // Arrange: Preparar datos de entrada
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);

        // Act: Ejecutar la consulta
        List<Price> prices = priceJpaAdapter.findApplicablePrices(35455L, 1, applicationDate);

        // Assert: Verificar resultados
        assertThat(prices).isNotEmpty();
        assertThat(prices).hasSize(2); // Precio base + precio promocional

        // Primer precio: Promocional con mayor prioridad
        Price firstPrice = prices.get(0);
        assertThat(firstPrice.getPriority()).isEqualTo(1);
        assertThat(firstPrice.getPriceList()).isEqualTo(2);
        assertThat(firstPrice.getPrice()).isEqualByComparingTo(new BigDecimal("25.45"));

        // Segundo precio: Base con menor prioridad
        Price secondPrice = prices.get(1);
        assertThat(secondPrice.getPriority()).isEqualTo(0);
        assertThat(secondPrice.getPriceList()).isEqualTo(1);
        assertThat(secondPrice.getPrice()).isEqualByComparingTo(new BigDecimal("35.50"));
    }

    /**
     * Test 3: Petición a las 21:00 del día 14 del producto 35455 para la brand 1 (ZARA)
     * <p>
     * Caso esperado: En este momento solo aplica el precio base.
     * El precio promocional (15:00-18:30) ya finalizó.
     * </p>
     * <p>
     * Validaciones:
     * <ul>
     *   <li>Se encuentra exactamente 1 precio</li>
     *   <li>Es el precio base con PRICE_LIST=1</li>
     *   <li>Tiene prioridad 0</li>
     * </ul>
     * </p>
     */
    @Test
    @DisplayName("Debe encontrar precios aplicables desde la BD para el Test 3 (21:00 día 14)")
    void shouldFindApplicablePricesTest3() {
        // Arrange: Preparar datos de entrada
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 21, 0);

        // Act: Ejecutar la consulta
        List<Price> prices = priceJpaAdapter.findApplicablePrices(35455L, 1, applicationDate);

        // Assert: Verificar resultados
        assertThat(prices).isNotEmpty();
        assertThat(prices).hasSize(1);

        Price price = prices.get(0);
        assertThat(price.getPriceList()).isEqualTo(1);
        assertThat(price.getPriority()).isEqualTo(0);
        assertThat(price.getPrice()).isEqualByComparingTo(new BigDecimal("35.50"));
    }

    /**
     * Test 4: Petición a las 10:00 del día 15 del producto 35455 para la brand 1 (ZARA)
     * <p>
     * Caso esperado: En este momento aplican 2 precios:
     * <ul>
     *   <li>Precio base (PRICE_LIST=1, PRIORITY=0)</li>
     *   <li>Precio promocional mañana (PRICE_LIST=3, PRIORITY=1) - 00:00 a 11:00</li>
     * </ul>
     * </p>
     * <p>
     * Validaciones:
     * <ul>
     *   <li>Se encuentran exactamente 2 precios</li>
     *   <li>El primero tiene PRICE_LIST=3 y precio 30.50 EUR</li>
     *   <li>El primero tiene mayor prioridad (1)</li>
     * </ul>
     * </p>
     */
    @Test
    @DisplayName("Debe encontrar precios aplicables desde la BD para el Test 4 (10:00 día 15)")
    void shouldFindApplicablePricesTest4() {
        // Arrange: Preparar datos de entrada
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 15, 10, 0);

        // Act: Ejecutar la consulta
        List<Price> prices = priceJpaAdapter.findApplicablePrices(35455L, 1, applicationDate);

        // Assert: Verificar resultados
        assertThat(prices).isNotEmpty();
        assertThat(prices).hasSize(2);

        Price firstPrice = prices.get(0);
        assertThat(firstPrice.getPriceList()).isEqualTo(3);
        assertThat(firstPrice.getPriority()).isEqualTo(1);
        assertThat(firstPrice.getPrice()).isEqualByComparingTo(new BigDecimal("30.50"));
    }

    /**
     * Test 5: Petición a las 21:00 del día 16 del producto 35455 para la brand 1 (ZARA)
     * <p>
     * Caso esperado: En este momento aplican 2 precios:
     * <ul>
     *   <li>Precio base (PRICE_LIST=1, PRIORITY=0)</li>
     *   <li>Precio promocional tarde (PRICE_LIST=4, PRIORITY=1) - desde 16:00 del día 15</li>
     * </ul>
     * </p>
     * <p>
     * Validaciones:
     * <ul>
     *   <li>Se encuentran exactamente 2 precios</li>
     *   <li>El primero tiene PRICE_LIST=4 y precio 38.95 EUR</li>
     *   <li>El primero tiene mayor prioridad (1)</li>
     * </ul>
     * </p>
     */
    @Test
    @DisplayName("Debe encontrar precios aplicables desde la BD para el Test 5 (21:00 día 16)")
    void shouldFindApplicablePricesTest5() {
        // Arrange: Preparar datos de entrada
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 16, 21, 0);

        // Act: Ejecutar la consulta
        List<Price> prices = priceJpaAdapter.findApplicablePrices(35455L, 1, applicationDate);

        // Assert: Verificar resultados
        assertThat(prices).isNotEmpty();
        assertThat(prices).hasSize(2);

        Price firstPrice = prices.get(0);
        assertThat(firstPrice.getPriceList()).isEqualTo(4);
        assertThat(firstPrice.getPriority()).isEqualTo(1);
        assertThat(firstPrice.getPrice()).isEqualByComparingTo(new BigDecimal("38.95"));
    }

    /**
     * Test de caso borde: No existen precios para los criterios especificados.
     * <p>
     * Verifica que cuando no hay coincidencias, el adaptador retorna una lista
     * vacía en lugar de null o lanzar excepción.
     * </p>
     * <p>
     * Validaciones:
     * <ul>
     *   <li>La lista retornada no es null</li>
     *   <li>La lista está vacía</li>
     * </ul>
     * </p>
     */
    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay precios que coincidan")
    void shouldReturnEmptyWhenNoPricesMatch() {
        // Arrange: Producto que no existe en los datos
        LocalDateTime applicationDate = LocalDateTime.of(2019, 1, 1, 0, 0);

        // Act: Ejecutar la consulta
        List<Price> prices = priceJpaAdapter.findApplicablePrices(99999L, 1, applicationDate);

        // Assert: Verificar que retorna lista vacía
        assertThat(prices).isNotNull();
        assertThat(prices).isEmpty();
    }

    /**
     * Test de ordenamiento: Verifica que los precios se retornan ordenados por prioridad.
     * <p>
     * Es crítico que el ordenamiento se realice correctamente porque el primer
     * elemento de la lista será el precio seleccionado por la capa de aplicación.
     * </p>
     * <p>
     * Validaciones:
     * <ul>
     *   <li>Los precios están ordenados por prioridad descendente</li>
     *   <li>El precio con mayor prioridad aparece primero</li>
     * </ul>
     * </p>
     */
    @Test
    @DisplayName("Debe retornar precios ordenados por prioridad descendente")
    void shouldReturnPricesOrderedByPriority() {
        // Arrange: Momento donde hay múltiples precios aplicables
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 16, 0);

        // Act: Ejecutar la consulta
        List<Price> prices = priceJpaAdapter.findApplicablePrices(35455L, 1, applicationDate);

        // Assert: Verificar ordenamiento por prioridad descendente
        assertThat(prices).isSortedAccordingTo((p1, p2) ->
            p2.getPriority().compareTo(p1.getPriority())
        );
    }

    /**
     * Test de mapeo completo: Verifica que todos los campos se mapean correctamente.
     * <p>
     * Garantiza que el mapper MapStruct está funcionando correctamente y que
     * ningún campo se pierde en la conversión de PriceEntity a Price.
     * </p>
     * <p>
     * Validaciones:
     * <ul>
     *   <li>Todos los campos del modelo de dominio están correctamente poblados</li>
     *   <li>Los valores coinciden con los datos de la base de datos</li>
     *   <li>Los tipos de datos son correctos (BigDecimal, LocalDateTime, etc.)</li>
     * </ul>
     * </p>
     */
    @Test
    @DisplayName("Debe mapear correctamente todos los campos de la entidad al dominio")
    void shouldMapAllFieldsCorrectly() {
        // Arrange
        LocalDateTime applicationDate = LocalDateTime.of(2020, 6, 14, 10, 0);

        // Act
        List<Price> prices = priceJpaAdapter.findApplicablePrices(35455L, 1, applicationDate);

        // Assert: Verificar que todos los campos están mapeados
        assertThat(prices).hasSize(1);

        Price price = prices.get(0);
        assertThat(price.getId()).isNotNull();
        assertThat(price.getProductId()).isEqualTo(35455L);
        assertThat(price.getBrandId()).isEqualTo(1);
        assertThat(price.getPriceList()).isEqualTo(1);
        assertThat(price.getStartDate()).isNotNull();
        assertThat(price.getEndDate()).isNotNull();
        assertThat(price.getPriority()).isNotNull();
        assertThat(price.getPrice()).isNotNull();
        assertThat(price.getCurrency()).isNotNull();

        // Verificar que las fechas son correctas
        assertThat(price.getStartDate()).isEqualTo(LocalDateTime.of(2020, 6, 14, 0, 0));
        assertThat(price.getEndDate()).isEqualTo(LocalDateTime.of(2020, 12, 31, 23, 59, 59));
    }
}
