package com.inditex.prices.infrastructure.adapter.in.rest;

import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.model.PriceQuery;
import com.inditex.prices.domain.port.in.GetApplicablePriceUseCase;
import com.inditex.prices.infrastructure.adapter.in.rest.dto.ErrorResponse;
import com.inditex.prices.infrastructure.adapter.in.rest.dto.PriceResponse;
import com.inditex.prices.infrastructure.adapter.in.rest.mapper.PriceRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Controlador REST para la gestión de consultas de precios.
 * Expone endpoints para consultar precios aplicables a productos en fechas específicas.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
@Tag(name = "Prices", description = "API de consulta de precios de productos")
public class PriceController {

    private final GetApplicablePriceUseCase getApplicablePriceUseCase;
    private final PriceRestMapper priceRestMapper;

    /**
     * Consulta el precio aplicable para un producto en una fecha específica.
     * Retorna el de mayor prioridad si hay múltiples coincidencias.
     *
     * @param applicationDate fecha de aplicación (yyyy-MM-dd'T'HH:mm:ss)
     * @param productId identificador del producto
     * @param brandId identificador de la marca
     * @return precio aplicable
     */
    @Operation(
            summary = "Obtener precio aplicable",
            description = "Consulta el precio aplicable de un producto para una marca y fecha específica. "
                    + "En caso de solapamiento de rangos de fechas, se aplica el precio de mayor prioridad."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Precio encontrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PriceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Parámetros de consulta inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró precio aplicable para los parámetros proporcionados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<PriceResponse> getApplicablePrice(
            @Parameter(
                    description = "Fecha de aplicación del precio",
                    example = "2020-06-14T10:00:00",
                    required = true
            )
            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
            LocalDateTime applicationDate,

            @Parameter(
                    description = "Identificador del producto",
                    example = "35455",
                    required = true
            )
            @RequestParam
            Long productId,

            @Parameter(
                    description = "Identificador de la cadena (marca)",
                    example = "1",
                    required = true
            )
            @RequestParam
            Integer brandId
    ) {
        log.info("Consultando precio - applicationDate={}, productId={}, brandId={}",
                applicationDate, productId, brandId);

        // Construir la consulta de dominio
        PriceQuery query = PriceQuery.builder()
                .applicationDate(applicationDate)
                .productId(productId)
                .brandId(brandId)
                .build();

        // Ejecutar caso de uso
        Price price = getApplicablePriceUseCase.getApplicablePrice(query);

        // Mapear a DTO de respuesta
        PriceResponse response = priceRestMapper.toResponse(price);

        log.info("Precio encontrado: priceList={}, price={} {}",
                response.getPriceList(), response.getPrice(), response.getCurrency());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
