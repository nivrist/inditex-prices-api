package com.inditex.prices.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra un precio aplicable
 * para los criterios especificados (producto, marca, fecha).
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
public class PriceNotFoundException extends RuntimeException {

    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param message mensaje descriptivo con los criterios de búsqueda
     */
    public PriceNotFoundException(String message) {
        super(message);
    }
}
