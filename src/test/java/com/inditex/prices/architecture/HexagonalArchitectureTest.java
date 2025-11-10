package com.inditex.prices.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Tests de validación de Arquitectura Hexagonal.
 * Verifica separación de capas, dependencias, naming conventions y uso de anotaciones.
 *
 * @author Irvin Monterroza
 * @version 1.0
 */
@DisplayName("Tests de Arquitectura Hexagonal")
class HexagonalArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        // Importar todas las clases del proyecto, excluyendo tests
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.inditex.prices");
    }

    // ========================================================================
    // Tests de Separación de Capas
    // ========================================================================

    /**
     * Verifica que la arquitectura tenga una separación clara de capas
     * siguiendo el patrón hexagonal: Domain <- Application <- Infrastructure
     */
    @Test
    @DisplayName("La arquitectura debe respetar la separación de capas hexagonal")
    void layeredArchitecture_shouldBeRespected() {
        ArchRule rule = layeredArchitecture()
                .consideringOnlyDependenciesInLayers()  // Ignorar dependencias de JDK
                .layer("Domain").definedBy("..domain..")
                .layer("Application").definedBy("..application..")
                .layer("Infrastructure").definedBy("..infrastructure..")

                // Reglas de acceso
                .whereLayer("Domain").mayNotAccessAnyLayer()
                .whereLayer("Application").mayOnlyAccessLayers("Domain")
                .whereLayer("Infrastructure").mayOnlyAccessLayers("Domain", "Application");

        rule.check(importedClasses);
    }

    // ========================================================================
    // Tests de Independencia del Dominio
    // ========================================================================

    /**
     * El dominio NO debe depender de Spring Framework.
     * Debe ser puro Java sin dependencias de infraestructura.
     */
    @Test
    @DisplayName("El dominio no debe depender de Spring Framework")
    void domainLayer_shouldNotDependOnSpring() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("org.springframework..");

        rule.check(importedClasses);
    }

    /**
     * El dominio NO debe depender de JPA/Hibernate.
     * Las entidades de dominio deben ser POJOs puros.
     */
    @Test
    @DisplayName("El dominio no debe depender de JPA/Hibernate")
    void domainLayer_shouldNotDependOnJPA() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "jakarta.persistence..",
                        "org.hibernate..",
                        "javax.persistence.."
                );

        rule.check(importedClasses);
    }

    /**
     * El dominio NO debe depender de REST/Web.
     * No debe conocer detalles de HTTP.
     */
    @Test
    @DisplayName("El dominio no debe depender de tecnologías web")
    void domainLayer_shouldNotDependOnWeb() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework.web..",
                        "jakarta.servlet..",
                        "javax.servlet.."
                );

        rule.check(importedClasses);
    }

    /**
     * El dominio NO debe depender de MapStruct.
     * El mapeo es responsabilidad de la infraestructura.
     */
    @Test
    @DisplayName("El dominio no debe depender de MapStruct")
    void domainLayer_shouldNotDependOnMapStruct() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage("org.mapstruct..");

        rule.check(importedClasses);
    }

    // ========================================================================
    // Tests de Naming Conventions
    // ========================================================================

    /**
     * Las interfaces de puertos de entrada deben terminar en UseCase.
     */
    @Test
    @DisplayName("Los puertos de entrada deben terminar en 'UseCase'")
    void inputPorts_shouldEndWithUseCase() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.port.in..")
                .and().areInterfaces()
                .should().haveSimpleNameEndingWith("UseCase");

        rule.check(importedClasses);
    }

    /**
     * Las interfaces de puertos de salida deben tener nombres significativos
     * (Repository, Gateway, etc.).
     */
    @Test
    @DisplayName("Los puertos de salida deben tener nombres significativos")
    void outputPorts_shouldHaveMeaningfulNames() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.port.out..")
                .and().areInterfaces()
                .should().haveNameMatching(".*Repository|.*Gateway|.*Port");

        rule.check(importedClasses);
    }

    /**
     * Los servicios de aplicación deben terminar en Service.
     */
    @Test
    @DisplayName("Los servicios de aplicación deben terminar en 'Service'")
    void applicationServices_shouldEndWithService() {
        ArchRule rule = classes()
                .that().resideInAPackage("..application.service..")
                .and().areNotInterfaces()
                .should().haveSimpleNameEndingWith("Service");

        rule.check(importedClasses);
    }

    /**
     * Los adaptadores deben terminar en Adapter, Controller, Mapper, etc.
     * Excluye clases internas generadas por Lombok y MapStruct.
     */
    @Test
    @DisplayName("Los adaptadores deben tener sufijos apropiados")
    void adapters_shouldHaveAppropriateSuffixes() {
        ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure.adapter..")
                .and().areNotInterfaces()
                .and().areNotAnnotations()
                .and().areNotMemberClasses()  // Excluir inner classes (Builder)
                .should().haveNameMatching(
                        ".*Adapter|.*Controller|.*Mapper|.*MapperImpl|.*Entity|.*Config|.*Handler|.*Response"
                );

        rule.check(importedClasses);
    }

    /**
     * Las excepciones del dominio deben terminar en Exception.
     */
    @Test
    @DisplayName("Las excepciones del dominio deben terminar en 'Exception'")
    void domainExceptions_shouldEndWithException() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain.exception..")
                .should().haveSimpleNameEndingWith("Exception")
                .andShould().beAssignableTo(RuntimeException.class);

        rule.check(importedClasses);
    }

    // ========================================================================
    // Tests de Anotaciones
    // ========================================================================

    /**
     * Las clases del dominio NO deben usar anotaciones de Spring.
     */
    @Test
    @DisplayName("Las clases del dominio no deben usar anotaciones de Spring")
    void domainClasses_shouldNotUseSpringAnnotations() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().beAnnotatedWith("org.springframework.stereotype.Service")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Component")
                .orShould().beAnnotatedWith("org.springframework.stereotype.Repository");

        rule.check(importedClasses);
    }

    /**
     * Los servicios de aplicación deben estar anotados con @Service.
     */
    @Test
    @DisplayName("Los servicios de aplicación deben estar anotados con @Service")
    void applicationServices_shouldBeAnnotatedWithService() {
        ArchRule rule = classes()
                .that().resideInAPackage("..application.service..")
                .and().haveSimpleNameEndingWith("Service")
                .should().beAnnotatedWith(org.springframework.stereotype.Service.class);

        rule.check(importedClasses);
    }

    /**
     * Los adaptadores de persistencia deben estar anotados con @Component.
     */
    @Test
    @DisplayName("Los adaptadores de persistencia deben estar anotados con @Component")
    void persistenceAdapters_shouldBeAnnotatedWithComponent() {
        ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure.adapter.out.persistence..")
                .and().haveSimpleNameEndingWith("Adapter")
                .should().beAnnotatedWith(org.springframework.stereotype.Component.class);

        rule.check(importedClasses);
    }

    /**
     * Los controllers REST deben estar anotados con @RestController.
     */
    @Test
    @DisplayName("Los controllers REST deben estar anotados con @RestController")
    void restControllers_shouldBeAnnotatedWithRestController() {
        ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure.adapter.in.rest..")
                .and().haveSimpleNameEndingWith("Controller")
                .should().beAnnotatedWith(org.springframework.web.bind.annotation.RestController.class);

        rule.check(importedClasses);
    }

    /**
     * Los mappers de MapStruct deben estar anotados con @Mapper.
     */
    @Test
    @DisplayName("Los mappers deben estar anotados con @Mapper de MapStruct")
    void mappers_shouldBeAnnotatedWithMapStructMapper() {
        ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure..")
                .and().haveSimpleNameEndingWith("Mapper")
                .and().areInterfaces()
                .should().beAnnotatedWith(org.mapstruct.Mapper.class);

        rule.check(importedClasses);
    }

    // ========================================================================
    // Tests de Puertos y Adaptadores
    // ========================================================================

    /**
     * Los puertos (interfaces) deben estar en el paquete domain.port.
     */
    @Test
    @DisplayName("Los puertos deben residir en domain.port")
    void ports_shouldResideInDomainPortPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameContaining("Port")
                .or().haveSimpleNameEndingWith("UseCase")
                .or().haveSimpleNameEndingWith("Repository")
                .and().areInterfaces()
                .and().resideInAPackage("..domain..")
                .should().resideInAPackage("..domain.port..");

        rule.check(importedClasses);
    }

    /**
     * Los adaptadores deben estar en el paquete infrastructure.adapter.
     */
    @Test
    @DisplayName("Los adaptadores deben residir en infrastructure.adapter")
    void adapters_shouldResideInInfrastructureAdapterPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Adapter")
                .and().areNotInterfaces()
                .should().resideInAPackage("..infrastructure.adapter..");

        rule.check(importedClasses);
    }

    /**
     * Los adaptadores de entrada deben implementar casos de uso (puertos de entrada).
     */
    @Test
    @DisplayName("Los adaptadores de entrada (REST) no deben implementar directamente UseCase")
    void restAdapters_shouldNotImplementUseCaseDirectly() {
        // Los controllers delegan en el servicio de aplicación, no implementan UseCase
        ArchRule rule = noClasses()
                .that().resideInAPackage("..infrastructure.adapter.in.rest..")
                .should().implement("..domain.port.in..");

        rule.check(importedClasses);
    }

    /**
     * Los adaptadores de salida deben implementar puertos de salida.
     */
    @Test
    @DisplayName("Los adaptadores de salida deben implementar puertos de salida")
    void outboundAdapters_shouldImplementOutputPorts() {
        ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure.adapter.out..")
                .and().haveSimpleNameEndingWith("Adapter")
                .should().dependOnClassesThat().resideInAPackage("..domain.port.out..");

        rule.check(importedClasses);
    }

    // ========================================================================
    // Tests de Inmutabilidad y Pureza del Dominio
    // ========================================================================

    /**
     * El dominio debe ser independiente de Lombok también en tiempo de ejecución.
     * Nota: No podemos verificar @Value de Lombok porque tiene @Retention(SOURCE),
     * pero podemos verificar que no hay dependencias innecesarias.
     */
    @Test
    @DisplayName("El dominio no debe depender de bibliotecas de utilidades externas innecesarias")
    void domainLayer_shouldNotDependOnExternalUtilityLibraries() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.apache.commons..",
                        "com.google.common.."
                );

        rule.check(importedClasses);
    }

    // ========================================================================
    // Tests de Responsabilidad de Paquetes
    // ========================================================================

    /**
     * El paquete domain.model solo debe contener entidades y value objects.
     */
    @Test
    @DisplayName("domain.model no debe contener clases de infraestructura")
    void domainModel_shouldNotContainInfrastructureClasses() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain.model..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..infrastructure..",
                        "org.springframework..",
                        "jakarta.persistence.."
                );

        rule.check(importedClasses);
    }

    /**
     * El paquete infrastructure.config solo debe contener clases de configuración.
     */
    @Test
    @DisplayName("infrastructure.config solo debe contener clases de configuración")
    void infrastructureConfig_shouldOnlyContainConfigClasses() {
        ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure.config..")
                .should().beAnnotatedWith(org.springframework.context.annotation.Configuration.class)
                .orShould().haveSimpleNameEndingWith("Config");

        rule.check(importedClasses);
    }
}
