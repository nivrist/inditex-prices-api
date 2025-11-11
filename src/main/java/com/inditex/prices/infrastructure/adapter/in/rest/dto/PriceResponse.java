package com.inditex.prices.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para consultas de precios.
 * Representa el precio aplicable a un producto en una fecha específica.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de precio aplicable para un producto")
public class PriceResponse {

    @Schema(description = "Identificador del producto", example = "35455")
    @JsonProperty("productId")
    private Long productId;

    @Schema(description = "Identificador de la cadena (marca)", example = "1")
    @JsonProperty("brandId")
    private Integer brandId;

    @Schema(description = "Identificador de la tarifa de precios aplicable", example = "1")
    @JsonProperty("priceList")
    private Integer priceList;

    @Schema(description = "Fecha de inicio de aplicación del precio",
            example = "2020-06-14T00:00:00")
    @JsonProperty("startDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @Schema(description = "Fecha de fin de aplicación del precio",
            example = "2020-12-31T23:59:59")
    @JsonProperty("endDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    @Schema(description = "Precio final de venta", example = "35.50")
    @JsonProperty("price")
    private BigDecimal price;

    @Schema(description = "Código ISO de la moneda", example = "EUR")
    @JsonProperty("currency")
    private String currency;
}
