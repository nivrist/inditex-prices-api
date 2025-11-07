-- =====================================================
-- Tabla de Marcas/Cadenas del Grupo
-- =====================================================
CREATE TABLE IF NOT EXISTS BRANDS (
    ID INTEGER PRIMARY KEY,
    NAME VARCHAR(100) NOT NULL,
    DESCRIPTION VARCHAR(255)
);

-- =====================================================
-- Tabla de Precios
-- =====================================================
CREATE TABLE IF NOT EXISTS PRICES (
    ID BIGINT AUTO_INCREMENT PRIMARY KEY,
    BRAND_ID INTEGER NOT NULL,
    START_DATE TIMESTAMP NOT NULL,
    END_DATE TIMESTAMP NOT NULL,
    PRICE_LIST INTEGER NOT NULL,
    PRODUCT_ID BIGINT NOT NULL,
    PRIORITY INTEGER NOT NULL,
    PRICE DECIMAL(10, 2) NOT NULL,
    CURR VARCHAR(3) NOT NULL,

    -- Restricción de clave foránea
    CONSTRAINT fk_prices_brand
        FOREIGN KEY (BRAND_ID)
        REFERENCES BRANDS(ID)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    -- Restricción para evitar duplicados
    CONSTRAINT uq_price_entry
        UNIQUE (BRAND_ID, PRODUCT_ID, PRICE_LIST, START_DATE)
);

-- =====================================================
-- Índices para Optimización de Consultas
-- =====================================================

-- Índice compuesto para la consulta principal
CREATE INDEX IF NOT EXISTS idx_brand_product_dates
    ON PRICES (BRAND_ID, PRODUCT_ID, START_DATE, END_DATE);

-- Índice para ordenamiento por prioridad
CREATE INDEX IF NOT EXISTS idx_priority
    ON PRICES (PRIORITY DESC);

-- Índice para búsquedas por rango de fechas
CREATE INDEX IF NOT EXISTS idx_date_range
    ON PRICES (START_DATE, END_DATE);
