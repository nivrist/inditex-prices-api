# Inditex Prices API

API REST para consulta de precios de productos con arquitectura hexagonal y DDD.

## Descripción

Sistema de consulta de precios aplicables a productos de diferentes marcas. Cuando múltiples precios se solapan en fechas, se aplica el de mayor prioridad.

**Características:**
- Arquitectura Hexagonal (Ports & Adapters)
- Domain-Driven Design (DDD)
- 76 tests (unitarios, integración, E2E, arquitectura)
- Cobertura de código: 93.41% (JaCoCo)
- Validación de arquitectura con ArchUnit
- Calidad de código con CheckStyle
- Documentación OpenAPI/Swagger

## Stack Tecnológico

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17 | Lenguaje |
| Spring Boot | 3.2.1 | Framework |
| H2 Database | Runtime | Base de datos en memoria |
| Spring Data JPA | 3.2.x | ORM |
| Lombok | 1.18.30 | Reducción de boilerplate |
| MapStruct | 1.5.5 | Mapeo DTO/Entity |
| SpringDoc OpenAPI | 2.3.0 | Documentación API |
| JaCoCo | 0.8.11 | Cobertura de código |
| CheckStyle | 3.3.1 | Calidad de código |
| ArchUnit | 1.3.0 | Tests de arquitectura |
| REST Assured | 5.4.0 | Tests E2E |

## Requisitos

- Java 17+ (LTS)
- Maven 3.8+

**Nota sobre Java:** El proyecto está configurado para Java 17. Si tienes múltiples versiones de Java instaladas, asegúrate de que Maven use Java 17 para evitar warnings de JaCoCo:

```bash
# Crear archivo ~/.mavenrc para que Maven use Java 17
echo 'export JAVA_HOME=/path/to/java-17' > ~/.mavenrc

# macOS con Homebrew
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home' > ~/.mavenrc

# Verificar versión de Java que usa Maven
mvn -version
```

## Instalación y Ejecución

```bash
# Clonar repositorio
git clone https://github.com/nivrist/inditex-prices-api.git
cd inditex-prices-api

# Compilar
mvn clean install

# Ejecutar aplicación (perfil local por defecto)
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## Perfiles de Configuración

El proyecto incluye 3 perfiles para diferentes ambientes:

| Perfil | Uso | Base de Datos | Logs | Consola H2 |
|--------|-----|---------------|------|------------|
| **local** | Desarrollo local (defecto) | H2 en memoria | DEBUG | ✅ Habilitada |
| **dev** | Entorno desarrollo compartido | H2 en memoria | INFO | ✅ Habilitada |
| **prod** | Producción | PostgreSQL | WARN | ❌ Deshabilitada |

**Activar perfil específico:**

```bash
# Opción 1: Sin variable de entorno (usa local por defecto)
mvn spring-boot:run

# Opción 2: Con variable de entorno
export SPRING_PROFILE=dev
mvn spring-boot:run

# Opción 3: Con parámetro de Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Opción 4: Perfil prod con todas las variables
export SPRING_PROFILE=prod
export DB_URL=jdbc:postgresql://localhost:5432/pricesdb
export DB_USER=postgres
export DB_PASSWORD=secret
mvn spring-boot:run

# Opción 5: Al ejecutar JAR
java -jar target/prices-api-1.0.0.jar --spring.profiles.active=dev
```

## Tests

```bash
# Todos los tests (76 tests)
mvn test

# Solo tests unitarios
mvn test -Dtest="*Test"

# Solo tests de integración
mvn test -Dtest="*IntegrationTest"

# Solo tests E2E
mvn test -Dtest="*SystemTest"

# Solo tests de arquitectura
mvn test -Dtest="HexagonalArchitectureTest"
```

**Desglose de tests:**
- 17 tests de dominio
- 8 tests de aplicación
- 8 tests de integración
- 21 tests E2E (incluye validación de parámetros negativos/cero)
- 22 tests de arquitectura
- **Total: 76 tests**

## Calidad de Código

### CheckStyle

```bash
# Validar con CheckStyle
mvn checkstyle:check
```

Configuración en `checkstyle.xml`:
- Max method length: 50 líneas
- Max parameters: 5 por método
- Max line length: 120 caracteres
- Validaciones de naming y JavaDoc

### Cobertura de Código (JaCoCo)

**Cobertura actual: 93.41%**

```bash
# Generar reporte de cobertura (automático con mvn test)
mvn clean test

# Ver reporte HTML
open target/site/jacoco/index.html

# Verificar umbrales de cobertura (falla si < 85%)
mvn verify
```

**Archivos generados:**
- `target/site/jacoco/index.html` - Reporte visual interactivo
- `target/site/jacoco/jacoco.xml` - Para CI/CD (SonarQube, Codecov)
- `target/site/jacoco/jacoco.csv` - Datos tabulares

**Umbrales configurados:**
- Cobertura de líneas: 85% mínimo
- Cobertura de branches: 85% mínimo

**Exclusiones:**
- Clase principal (`PricesApplication`)
- Configuraciones Spring (`config/**`)
- Entidades JPA (solo getters/setters)
- DTOs (sin lógica de negocio)
- Mappers generados por MapStruct
- Modelos de dominio inmutables (Lombok `@Value`)
- Excepciones de dominio

**Cobertura por componente:**
- `PriceController`: 100%
- `PriceService`: 100%
- `PriceJpaAdapter`: 100%
- `GlobalExceptionHandler`: 82.5%

### Validación Completa

```bash
# Ejecutar todo: tests + checkstyle + cobertura
mvn clean verify
```

## API REST

### Endpoint Principal

**GET** `/api/prices`

Consulta el precio aplicable para un producto en una fecha específica.

**Parámetros:**
- `applicationDate` (DateTime): Fecha de aplicación (ISO-8601)
- `productId` (Integer): Identificador del producto
- `brandId` (Integer): Identificador de la marca

**Ejemplo:**
```bash
curl "http://localhost:8080/api/prices?applicationDate=2020-06-14T10:00:00&productId=35455&brandId=1"
```

**Respuesta exitosa (200):**
```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 1,
  "startDate": "2020-06-14T00:00:00",
  "endDate": "2020-12-31T23:59:59",
  "price": 35.50,
  "currency": "EUR"
}
```

**Errores:**
- `400 Bad Request`: Parámetros faltantes o inválidos
- `404 Not Found`: No existe precio aplicable
- `500 Internal Server Error`: Error del servidor

### Documentación Swagger

Disponible en: `http://localhost:8080/api/swagger-ui.html`

## Base de Datos

### Schema

```sql
-- Tabla de marcas
CREATE TABLE BRANDS (
    ID BIGINT PRIMARY KEY,
    NAME VARCHAR(100),
    DESCRIPTION VARCHAR(255)
);

-- Tabla de precios
CREATE TABLE PRICES (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    BRAND_ID BIGINT,
    START_DATE TIMESTAMP,
    END_DATE TIMESTAMP,
    PRICE_LIST INTEGER,
    PRODUCT_ID INTEGER,
    PRIORITY INTEGER,
    PRICE DECIMAL(10, 2),
    CURRENCY VARCHAR(3),
    FOREIGN KEY (BRAND_ID) REFERENCES BRANDS(ID)
);
```

### Consola H2

Disponible en: `http://localhost:8080/h2-console`

**Credenciales:**
- JDBC URL: `jdbc:h2:mem:pricesdb`
- User: `sa`
- Password: _(vacío)_

## Arquitectura

### Estructura de Capas

```
src/main/java/com/inditex/prices/
├── domain/                 # Capa de dominio (sin dependencias externas)
│   ├── model/             # Entidades y Value Objects
│   ├── port/in/           # Puertos de entrada (Use Cases)
│   ├── port/out/          # Puertos de salida (Repositories)
│   └── exception/         # Excepciones de dominio
│
├── application/           # Capa de aplicación
│   └── service/           # Implementación de Use Cases
│
└── infrastructure/        # Capa de infraestructura
    ├── adapter/
    │   ├── in/rest/       # Adaptador REST (Controllers, DTOs)
    │   └── out/persistence/ # Adaptador JPA (Entities, Repositories)
    └── config/            # Configuración Spring
```

### Principios Aplicados

**SOLID:**
- Single Responsibility: Cada clase tiene una única responsabilidad
- Open/Closed: Extensible mediante puertos/adaptadores
- Liskov Substitution: Interfaces bien definidas
- Interface Segregation: Interfaces específicas por caso de uso
- Dependency Inversion: Dependencias hacia abstracciones (puertos)

**DDD:**
- Value Objects inmutables (`Price`, `PriceQuery`)
- Repository Pattern (puerto `PriceRepository`)
- Ubiquitous Language (términos del negocio)
- Domain Exceptions (`PriceNotFoundException`)

## Comandos Útiles

```bash
# Compilar sin tests
mvn clean install -DskipTests

# Generar JAR ejecutable
mvn package

# Ejecutar JAR
java -jar target/prices-api-1.0.0.jar

# Limpiar proyecto
mvn clean

# Ver dependencias
mvn dependency:tree
```

## Métricas de Calidad

| Métrica | Valor | Estado |
|---------|-------|--------|
| **Tests** | 76 tests | ✅ 100% passing |
| **Cobertura de Código** | 93.41% | ✅ Excelente |
| **CheckStyle Violations** | 0 | ✅ Sin violaciones |
| **Tests de Arquitectura** | 22 reglas | ✅ Todas cumplen |
| **Componentes 100% cubiertos** | 3 de 4 | ✅ PriceController, PriceService, PriceJpaAdapter |

**Distribución de Tests:**
- 🧪 Unitarios: 25 tests
- 🔗 Integración: 8 tests
- 🌐 E2E (REST Assured): 21 tests
- 🏛️ Arquitectura (ArchUnit): 22 tests

**Tiempo de ejecución:** ~13 segundos

## Autor

Irvin Monterroza

## Licencia

Proyecto de evaluación técnica para Inditex.
