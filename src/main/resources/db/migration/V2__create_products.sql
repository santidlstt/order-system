-- =====================================================
-- V2: Crear tabla de productos
-- =====================================================

CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          price_amount DECIMAL(19, 2) NOT NULL,
                          price_currency VARCHAR(3) NOT NULL DEFAULT 'USD',
                          stock INTEGER NOT NULL DEFAULT 0,
                          active BOOLEAN NOT NULL DEFAULT true,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT products_price_positive CHECK (price_amount >= 0),
                          CONSTRAINT products_stock_non_negative CHECK (stock >= 0)
);

-- Índices para mejorar performance
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_active ON products(active);

-- =====================================================
-- Datos iniciales: Productos de ejemplo
-- =====================================================

INSERT INTO products (name, description, price_amount, price_currency, stock, active) VALUES
                                                                                          ('Laptop Dell XPS 13', 'Laptop ultradelgada con procesador Intel i7, 16GB RAM, 512GB SSD', 1299.99, 'USD', 15, true),
                                                                                          ('iPhone 15 Pro', 'Smartphone Apple con chip A17 Pro, 256GB, cámara profesional', 999.99, 'USD', 25, true),
                                                                                          ('Sony WH-1000XM5', 'Auriculares inalámbricos con cancelación de ruido', 399.99, 'USD', 40, true),
                                                                                          ('Samsung Galaxy Tab S9', 'Tablet Android premium con S Pen incluido', 799.99, 'USD', 20, true),
                                                                                          ('Apple Watch Series 9', 'Smartwatch con GPS, monitor de salud avanzado', 429.99, 'USD', 30, true),
                                                                                          ('MacBook Pro 14"', 'Laptop profesional con chip M3 Pro, 18GB RAM, 512GB SSD', 1999.99, 'USD', 10, true),
                                                                                          ('Logitech MX Master 3S', 'Mouse inalámbrico ergonómico para productividad', 99.99, 'USD', 50, true),
                                                                                          ('Mechanical Keyboard', 'Teclado mecánico RGB con switches Cherry MX', 149.99, 'USD', 35, true),
                                                                                          ('Monitor LG UltraWide', 'Monitor 34" curvo 1440p, ideal para multitarea', 599.99, 'USD', 12, true),
                                                                                          ('AirPods Pro 2', 'Auriculares inalámbricos con cancelación activa de ruido', 249.99, 'USD', 60, true);