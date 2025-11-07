-- =====================================================
-- Datos Iniciales: Marcas
-- =====================================================
INSERT INTO BRANDS (ID, NAME, DESCRIPTION) VALUES
(1, 'ZARA', 'Cadena principal del grupo Inditex');

-- =====================================================
-- Datos Iniciales: Precios
-- =====================================================

-- Precio base (prioridad 0): Todo el periodo
INSERT INTO PRICES (BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURR)
VALUES (1, '2020-06-14 00:00:00', '2020-12-31 23:59:59', 1, 35455, 0, 35.50, 'EUR');

-- Precio promocional 1 (prioridad 1): Tarde del 14 de junio
INSERT INTO PRICES (BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURR)
VALUES (1, '2020-06-14 15:00:00', '2020-06-14 18:30:00', 2, 35455, 1, 25.45, 'EUR');

-- Precio promocional 2 (prioridad 1): Mañana del 15 de junio
INSERT INTO PRICES (BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURR)
VALUES (1, '2020-06-15 00:00:00', '2020-06-15 11:00:00', 3, 35455, 1, 30.50, 'EUR');

-- Precio promocional 3 (prioridad 1): Desde tarde del 15 hasta fin de año
INSERT INTO PRICES (BRAND_ID, START_DATE, END_DATE, PRICE_LIST, PRODUCT_ID, PRIORITY, PRICE, CURR)
VALUES (1, '2020-06-15 16:00:00', '2020-12-31 23:59:59', 4, 35455, 1, 38.95, 'EUR');
