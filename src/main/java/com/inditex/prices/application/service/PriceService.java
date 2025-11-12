package com.inditex.prices.application.service;

import com.inditex.prices.domain.exception.InvalidQueryException;
import com.inditex.prices.domain.exception.PriceNotFoundException;
import com.inditex.prices.domain.model.Price;
import com.inditex.prices.domain.model.PriceQuery;
import com.inditex.prices.domain.port.in.GetApplicablePriceUseCase;
import com.inditex.prices.domain.port.out.PriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Implementación del caso de uso de obtención de precio aplicable.
 * Orquesta la validación, búsqueda en repositorio y selección por prioridad.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PriceService implements GetApplicablePriceUseCase {

    private final PriceRepository priceRepository;

    /**
     * Obtiene el precio aplicable validando parámetros, filtrando por vigencia
     * y seleccionando el de mayor prioridad en caso de múltiples coincidencias.
     *
     * @param query criterios de búsqueda (fecha, producto, marca)
     * @return precio aplicable con mayor prioridad
     * @throws InvalidQueryException si los parámetros no son válidos
     * @throws PriceNotFoundException si no existe precio aplicable
     */
    @Override
    @Transactional(readOnly = true)
    public Price getApplicablePrice(PriceQuery query) {
        log.debug("Buscando precio aplicable para la consulta: {}", query);

        // Validar la consulta según las reglas de negocio del dominio
        try {
            query.validate();
        } catch (IllegalArgumentException e) {
            log.error("Parámetros de consulta inválidos: {}", e.getMessage());
            throw new InvalidQueryException(e.getMessage());
        }

        // Buscar precios candidatos que coincidan con producto, marca y fecha
        List<Price> applicablePrices = priceRepository.findApplicablePrices(
                query.getProductId(),
                query.getBrandId(),
                query.getApplicationDate()
        );

        log.debug("Se encontraron {} precios potenciales para producto {} y marca {}",
                applicablePrices.size(),
                query.getProductId(),
                query.getBrandId());

        // Filtrar por vigencia temporal y seleccionar el de mayor prioridad
        // Utilizamos Stream API para aplicar las reglas de negocio de forma funcional
        return applicablePrices.stream()
                .filter(price -> price.isApplicableAt(query.getApplicationDate()))
                .max(Comparator.comparing(Price::getPriority))
                .orElseThrow(() -> {
                    String message = String.format(
                            "No se encontró precio aplicable para producto %d, marca %d en fecha %s",
                            query.getProductId(),
                            query.getBrandId(),
                            query.getApplicationDate()
                    );
                    log.warn(message);
                    return new PriceNotFoundException(message);
                });
    }
}
