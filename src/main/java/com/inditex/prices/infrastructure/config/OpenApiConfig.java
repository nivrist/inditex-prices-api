package com.inditex.prices.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación interactiva de la API.
 * Proporciona información descriptiva sobre la API de precios y sus endpoints.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    /**
     * Configura la especificación OpenAPI de la aplicación.
     *
     * @return objeto OpenAPI con metadata
     */
    @Bean
    public OpenAPI pricesOpenApi() {
        return new OpenAPI()
                .info(buildApiInfo())
                .servers(buildServers());
    }

    /**
     * Construye la información general de la API.
     *
     * @return metadata de la API
     */
    private Info buildApiInfo() {
        return new Info()
                .title("Inditex Prices API")
                .description("""
                        API REST para consulta de precios de productos del grupo Inditex.

                        ## Funcionalidades

                        - Consulta de precios aplicables por producto, marca y fecha
                        - Desambiguación automática por prioridad en caso de solapamiento de rangos
                        - Manejo de errores con códigos HTTP estándar

                        ## Arquitectura

                        Implementada con **Arquitectura Hexagonal (Ports & Adapters)** usando:
                        - Domain-Driven Design (DDD)
                        - Spring Boot 3.2
                        - Java 17
                        - Base de datos H2 en memoria
                        """)
                .version("1.0.0")
                .contact(buildContact())
                .license(buildLicense());
    }

    /**
     * Construye la información de contacto.
     *
     * @return información de contacto
     */
    private Contact buildContact() {
        return new Contact()
                .name("Inditex Development Team")
                .email("dev@inditex.com")
                .url("https://www.inditex.com");
    }

    /**
     * Construye la información de licencia.
     *
     * @return información de licencia
     */
    private License buildLicense() {
        return new License()
                .name("Inditex Proprietary License")
                .url("https://www.inditex.com/license");
    }

    /**
     * Construye la lista de servidores disponibles.
     *
     * @return lista de servidores
     */
    private List<Server> buildServers() {
        Server localServer = new Server()
                .url("http://localhost:8080" + contextPath)
                .description("Servidor de desarrollo local");

        return List.of(localServer);
    }
}
