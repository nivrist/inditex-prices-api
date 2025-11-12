package com.inditex.prices.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que mapea a la tabla BRANDS.
 * Representa una marca/cadena comercial (ZARA, Pull&Bear, etc.).
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@Entity
@Table(name = "BRANDS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandEntity {

    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;
}
