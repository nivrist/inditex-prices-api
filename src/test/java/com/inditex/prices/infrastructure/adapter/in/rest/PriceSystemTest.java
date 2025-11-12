package com.inditex.prices.infrastructure.adapter.in.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests de Sistema (E2E) para la API REST de precios.
 * Valida el comportamiento completo de la aplicación desde la capa HTTP hasta la base de datos.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb-system",
                "spring.jpa.hibernate.ddl-auto=none",
                "spring.sql.init.mode=always"
        }
)
@DisplayName("Tests de Sistema - API de Precios")
class PriceSystemTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
    }

    // ========================================================================
    // Tests de los 5 casos de prueba requeridos
    // ========================================================================

    /**
     * Test 1: día 14 a las 10:00 debe retornar precio base.
     */
    @Test
    @DisplayName("Test 1 - Día 14 a las 10:00: debe retornar precio base")
    void test1_requestAt10AMOnDay14_shouldReturnBasePrice() {
        given()
                .queryParam("applicationDate", "2020-06-14T10:00:00")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("productId", equalTo(35455))
                .body("brandId", equalTo(1))
                .body("priceList", equalTo(1))
                .body("startDate", equalTo("2020-06-14T00:00:00"))
                .body("endDate", equalTo("2020-12-31T23:59:59"))
                .body("price", equalTo(35.5f))
                .body("currency", equalTo("EUR"));
    }

    /**
     * Test 2: día 14 a las 16:00 debe retornar precio promocional.
     */
    @Test
    @DisplayName("Test 2 - Día 14 a las 16:00: debe retornar precio promocional")
    void test2_requestAt4PMOnDay14_shouldReturnPromotionalPrice() {
        given()
                .queryParam("applicationDate", "2020-06-14T16:00:00")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("productId", equalTo(35455))
                .body("brandId", equalTo(1))
                .body("priceList", equalTo(2))
                .body("startDate", equalTo("2020-06-14T15:00:00"))
                .body("endDate", equalTo("2020-06-14T18:30:00"))
                .body("price", equalTo(25.45f))
                .body("currency", equalTo("EUR"));
    }

    /**
     * Test 3: día 14 a las 21:00 debe retornar precio base.
     */
    @Test
    @DisplayName("Test 3 - Día 14 a las 21:00: debe retornar precio base")
    void test3_requestAt9PMOnDay14_shouldReturnBasePrice() {
        given()
                .queryParam("applicationDate", "2020-06-14T21:00:00")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("productId", equalTo(35455))
                .body("brandId", equalTo(1))
                .body("priceList", equalTo(1))
                .body("startDate", equalTo("2020-06-14T00:00:00"))
                .body("endDate", equalTo("2020-12-31T23:59:59"))
                .body("price", equalTo(35.5f))
                .body("currency", equalTo("EUR"));
    }

    /**
     * Test 4: día 15 a las 10:00 debe retornar precio promocional.
     */
    @Test
    @DisplayName("Test 4 - Día 15 a las 10:00: debe retornar precio promocional")
    void test4_requestAt10AMOnDay15_shouldReturnPromotionalPrice() {
        given()
                .queryParam("applicationDate", "2020-06-15T10:00:00")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("productId", equalTo(35455))
                .body("brandId", equalTo(1))
                .body("priceList", equalTo(3))
                .body("startDate", equalTo("2020-06-15T00:00:00"))
                .body("endDate", equalTo("2020-06-15T11:00:00"))
                .body("price", equalTo(30.5f))
                .body("currency", equalTo("EUR"));
    }

    /**
     * Test 5: día 16 a las 21:00 debe retornar precio promocional.
     */
    @Test
    @DisplayName("Test 5 - Día 16 a las 21:00: debe retornar precio promocional")
    void test5_requestAt9PMOnDay16_shouldReturnPromotionalPrice() {
        given()
                .queryParam("applicationDate", "2020-06-16T21:00:00")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("productId", equalTo(35455))
                .body("brandId", equalTo(1))
                .body("priceList", equalTo(4))
                .body("startDate", equalTo("2020-06-15T16:00:00"))
                .body("endDate", equalTo("2020-12-31T23:59:59"))
                .body("price", equalTo(38.95f))
                .body("currency", equalTo("EUR"));
    }

    // ========================================================================
    // Tests de validación de contratos
    // ========================================================================

    /**
     * Verifica que la respuesta contiene todos los campos requeridos.
     */
    @Test
    @DisplayName("Contrato - La respuesta exitosa debe contener todos los campos requeridos")
    void responseContract_shouldContainAllRequiredFields() {
        given()
                .queryParam("applicationDate", "2020-06-14T10:00:00")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("productId", notNullValue())
                .body("brandId", notNullValue())
                .body("priceList", notNullValue())
                .body("startDate", notNullValue())
                .body("endDate", notNullValue())
                .body("price", notNullValue())
                .body("currency", notNullValue());
    }

    /**
     * Verifica el Content-Type de la respuesta.
     */
    @Test
    @DisplayName("Contrato - La respuesta debe tener Content-Type application/json")
    void responseContract_shouldHaveJsonContentType() {
        given()
                .queryParam("applicationDate", "2020-06-14T10:00:00")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .contentType(ContentType.JSON);
    }

    // ========================================================================
    // Tests de manejo de errores
    // ========================================================================

    /**
     * Verifica que retorna 404 cuando no existe precio aplicable.
     */
    @Test
    @DisplayName("Error 404 - Debe retornar Not Found cuando no existe precio aplicable")
    void whenPriceNotFound_shouldReturn404() {
        given()
                .queryParam("applicationDate", "2019-01-01T10:00:00")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(404))
                .body("error", equalTo("Not Found"))
                .body("message", notNullValue())
                .body("timestamp", notNullValue())
                .body("path", equalTo("/api/prices"));
    }

    /**
     * Verifica que retorna 400 cuando falta applicationDate.
     */
    @Test
    @DisplayName("Error 400 - Debe retornar Bad Request cuando falta applicationDate")
    void whenMissingApplicationDate_shouldReturn400() {
        given()
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", notNullValue())
                .body("timestamp", notNullValue());
    }

    /**
     * Verifica que retorna 400 cuando falta productId.
     */
    @Test
    @DisplayName("Error 400 - Debe retornar Bad Request cuando falta productId")
    void whenMissingProductId_shouldReturn400() {
        given()
                .queryParam("applicationDate", "2020-06-14T10:00:00")
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", notNullValue());
    }

    /**
     * Verifica que retorna 400 cuando falta brandId.
     */
    @Test
    @DisplayName("Error 400 - Debe retornar Bad Request cuando falta brandId")
    void whenMissingBrandId_shouldReturn400() {
        given()
                .queryParam("applicationDate", "2020-06-14T10:00:00")
                .queryParam("productId", 35455)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", notNullValue());
    }

    /**
     * Verifica que retorna 400 con formato de fecha inválido.
     */
    @Test
    @DisplayName("Error 400 - Debe retornar Bad Request cuando el formato de fecha es inválido")
    void whenInvalidDateFormat_shouldReturn400() {
        given()
                .queryParam("applicationDate", "invalid-date")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"));
    }

    /**
     * Verifica que retorna 400 con productId no numérico.
     */
    @Test
    @DisplayName("Error 400 - Debe retornar Bad Request cuando productId no es numérico")
    void whenInvalidProductId_shouldReturn400() {
        given()
                .queryParam("applicationDate", "2020-06-14T10:00:00")
                .queryParam("productId", "invalid")
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"));
    }

    /**
     * Verifica que retorna 400 con brandId no numérico.
     */
    @Test
    @DisplayName("Error 400 - Debe retornar Bad Request cuando brandId no es numérico")
    void whenInvalidBrandId_shouldReturn400() {
        given()
                .queryParam("applicationDate", "2020-06-14T10:00:00")
                .queryParam("productId", 35455)
                .queryParam("brandId", "invalid")
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"));
    }

    // ========================================================================
    // Tests de casos límite
    // ========================================================================

    /**
     * Verifica que fecha en inicio exacto de rango es válida.
     */
    @Test
    @DisplayName("Caso límite - Fecha exactamente en el inicio del rango debe ser válida")
    void whenDateIsExactlyAtRangeStart_shouldReturnPrice() {
        given()
                .queryParam("applicationDate", "2020-06-14T00:00:00")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("priceList", equalTo(1));
    }

    /**
     * Verifica que fecha en fin exacto de rango es válida.
     */
    @Test
    @DisplayName("Caso límite - Fecha exactamente en el fin del rango debe ser válida")
    void whenDateIsExactlyAtRangeEnd_shouldReturnPrice() {
        given()
                .queryParam("applicationDate", "2020-12-31T23:59:59")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("price", notNullValue());
    }

    /**
     * Verifica que un segundo antes de la promoción retorna precio base.
     */
    @Test
    @DisplayName("Caso límite - Un segundo antes del inicio de la promoción debe retornar precio base")
    void whenDateIsOneSecondBeforePromotion_shouldReturnBasePrice() {
        // La promoción 2 comienza a las 15:00:00
        // Un segundo antes (14:59:59) debe retornar precio base
        given()
                .queryParam("applicationDate", "2020-06-14T14:59:59")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("priceList", equalTo(1))
                .body("price", equalTo(35.5f));
    }

    // ========================================================================
    // Tests de validación de valores negativos y cero
    // ========================================================================

    /**
     * Verifica que retorna 400 cuando productId es negativo.
     */
    @Test
    @DisplayName("Error 400 - Debe retornar Bad Request cuando productId es negativo")
    void whenProductIdIsNegative_shouldReturn400() {
        given()
                .queryParam("applicationDate", "2020-06-14T10:00:00")
                .queryParam("productId", -1)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", notNullValue())
                .body("timestamp", notNullValue())
                .body("path", equalTo("/api/prices"));
    }

    /**
     * Verifica que retorna 400 cuando productId es cero.
     */
    @Test
    @DisplayName("Error 400 - Debe retornar Bad Request cuando productId es cero")
    void whenProductIdIsZero_shouldReturn400() {
        given()
                .queryParam("applicationDate", "2020-06-14T10:00:00")
                .queryParam("productId", 0)
                .queryParam("brandId", 1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", notNullValue())
                .body("timestamp", notNullValue())
                .body("path", equalTo("/api/prices"));
    }

    /**
     * Verifica que retorna 400 cuando brandId es negativo.
     */
    @Test
    @DisplayName("Error 400 - Debe retornar Bad Request cuando brandId es negativo")
    void whenBrandIdIsNegative_shouldReturn400() {
        given()
                .queryParam("applicationDate", "2020-06-14T10:00:00")
                .queryParam("productId", 35455)
                .queryParam("brandId", -1)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", notNullValue())
                .body("timestamp", notNullValue())
                .body("path", equalTo("/api/prices"));
    }

    /**
     * Verifica que retorna 400 cuando brandId es cero.
     */
    @Test
    @DisplayName("Error 400 - Debe retornar Bad Request cuando brandId es cero")
    void whenBrandIdIsZero_shouldReturn400() {
        given()
                .queryParam("applicationDate", "2020-06-14T10:00:00")
                .queryParam("productId", 35455)
                .queryParam("brandId", 0)
                .when()
                .get("/prices")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("status", equalTo(400))
                .body("error", equalTo("Bad Request"))
                .body("message", notNullValue())
                .body("timestamp", notNullValue())
                .body("path", equalTo("/api/prices"));
    }
}
