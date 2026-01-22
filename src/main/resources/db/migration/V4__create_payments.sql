-- =====================================================
-- V4: Crear tabla de pagos
-- =====================================================

CREATE TABLE payments (
                          id BIGSERIAL PRIMARY KEY,
                          order_id BIGINT NOT NULL,
                          amount DECIMAL(19, 2) NOT NULL,
                          currency VARCHAR(3) NOT NULL DEFAULT 'USD',
                          status VARCHAR(20) NOT NULL,
                          payment_method VARCHAR(50),
                          transaction_id VARCHAR(255),

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id),
                          CONSTRAINT payments_amount_positive CHECK (amount >= 0),
                          CONSTRAINT payments_order_unique UNIQUE (order_id)  -- Un pedido solo puede tener un pago
);

-- Índices
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_payments_transaction_id ON payments(transaction_id);