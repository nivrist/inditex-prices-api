package com.inditex.prices.domain.exception;

/**
 * Excepción lanzada cuando los parámetros de consulta no cumplen
 * las reglas de validación (campos nulos, IDs inválidos, etc.).
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
public class InvalidQueryException extends RuntimeException {

    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message descripción de la validación que falló
     */
    public InvalidQueryException(String message) {
        super(message);
    }
}
