package com.inditex.prices.infrastructure.adapter.in.exception;

import com.inditex.prices.domain.exception.InvalidQueryException;
import com.inditex.prices.domain.exception.PriceNotFoundException;
import com.inditex.prices.infrastructure.adapter.in.rest.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

/**
 * Manejador global de excepciones para la API REST.
 * Captura excepciones de dominio y las transforma en respuestas HTTP apropiadas.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones cuando no se encuentra un precio aplicable.
     *
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta HTTP 404
     */
    @ExceptionHandler(PriceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePriceNotFoundException(
            PriceNotFoundException ex,
            WebRequest request) {

        log.warn("Precio no encontrado: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    /**
     * Maneja excepciones de validación de parámetros de consulta.
     *
     * @param ex excepción lanzada
     * @param request petición HTTP
     * @return respuesta HTTP 400
     */
    @ExceptionHandler(InvalidQueryException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQueryException(
            InvalidQueryException ex,
            WebRequest request) {

        log.warn("Parámetros de consulta inválidos: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(extractPath(request))
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    /**
     * Maneja excepciones cuando falta un parámetro requerido.
     *
     * @param ex excepción lanzada por Spring
     * @param request petición HTTP
     * @return respuesta HTTP 400
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex,
            WebRequest request) {

        log.warn("Falta parámetro requerido: {}", ex.getParameterName());

        String message = String.format(
                "Parámetro requerido '%s' no está presente",
                ex.getParameterName()
        );

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(extractPath(request))
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    /**
     * Maneja excepciones cuando un parámetro tiene un tipo incorrecto.
     *
     * @param ex excepción lanzada por Spring
     * @param request petición HTTP
     * @return respuesta HTTP 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {

        log.warn("Tipo de dato incorrecto para parámetro {}: {}", ex.getName(), ex.getValue());

        String message = String.format(
                "Parámetro '%s' tiene un valor inválido: '%s'",
                ex.getName(),
                ex.getValue()
        );

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(extractPath(request))
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    /**
     * Maneja excepciones genéricas no contempladas específicamente.
     *
     * @param ex excepción genérica
     * @param request petición HTTP
     * @return respuesta HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request) {

        log.error("Error inesperado", ex);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Ha ocurrido un error inesperado. Por favor, intente nuevamente.")
                .path(extractPath(request))
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    /**
     * Extrae la ruta de la petición HTTP.
     *
     * @param request petición HTTP
     * @return ruta de la petición
     */
    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
