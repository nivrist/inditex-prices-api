package com.inditex.prices.domain.port.in;

import com.inditex.prices.domain.exception.InvalidQueryException;
import com.inditex.prices.domain.exception.PriceNotFoundException;
import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.model.PriceQuery;

/**
 * Puerto de entrada para el caso de uso de obtención de precio aplicable.
 * Define el contrato para consultar el precio de un producto considerando marca, fecha y prioridad.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
public interface GetApplicablePriceUseCase {

    /**
     * Obtiene el precio aplicable para un producto en una fecha específica.
     * Si múltiples precios coinciden, retorna el de mayor prioridad.
     *
     * @param query parámetros de búsqueda (fecha, producto, marca)
     * @return precio aplicable con mayor prioridad
     * @throws InvalidQueryException si los parámetros no son válidos
     * @throws PriceNotFoundException si no existe precio aplicable
     * @throws NullPointerException si query es null
     */
    Price getApplicablePrice(PriceQuery query);
}
