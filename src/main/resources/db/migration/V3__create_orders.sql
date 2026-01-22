-- =====================================================
-- V3: Crear tablas de pedidos y items de pedidos
-- =====================================================

-- Tabla de pedidos
CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        total_amount DECIMAL(19, 2) NOT NULL,
                        total_currency VARCHAR(3) NOT NULL DEFAULT 'USD',

    -- Dirección de envío (embedded)
                        address_street VARCHAR(255),
                        address_city VARCHAR(100),
                        address_country VARCHAR(100),

                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
                        CONSTRAINT orders_total_positive CHECK (total_amount >= 0)
);

-- Tabla de items de pedido
CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             quantity INTEGER NOT NULL,
                             unit_price_amount DECIMAL(19, 2) NOT NULL,
                             unit_price_currency VARCHAR(3) NOT NULL DEFAULT 'USD',
                             subtotal_amount DECIMAL(19, 2) NOT NULL,
                             subtotal_currency VARCHAR(3) NOT NULL DEFAULT 'USD',

                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id),
                             CONSTRAINT order_items_quantity_positive CHECK (quantity > 0),
                             CONSTRAINT order_items_price_positive CHECK (unit_price_amount >= 0),
                             CONSTRAINT order_items_subtotal_positive CHECK (subtotal_amount >= 0)
);

-- Índices para mejorar performance
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);