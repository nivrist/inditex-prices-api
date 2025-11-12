package com.inditex.prices.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO estándar para respuestas de error de la API.
 * Proporciona información detallada sobre errores ocurridos durante el procesamiento de peticiones.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de error de la API")
public class ErrorResponse {

    @Schema(description = "Timestamp del error", example = "2024-01-15T10:30:00")
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "Código de estado HTTP", example = "404")
    @JsonProperty("status")
    private Integer status;

    @Schema(description = "Descripción breve del error", example = "Not Found")
    @JsonProperty("error")
    private String error;

    @Schema(description = "Mensaje descriptivo del error",
            example = "No se encontró precio aplicable para los parámetros proporcionados")
    @JsonProperty("message")
    private String message;

    @Schema(description = "Ruta de la petición que generó el error",
            example = "/api/prices")
    @JsonProperty("path")
    private String path;
}
