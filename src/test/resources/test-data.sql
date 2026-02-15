-- ============================================
-- DATOS DE PRUEBA PARA TESTS DE INTEGRACIÓN
-- ============================================

-- Limpiar datos existentes
DELETE FROM order_items;
DELETE FROM payments;
DELETE FROM orders;
DELETE FROM products;
DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM roles;

-- Insertar roles
INSERT INTO roles (id, name, created_at, updated_at) VALUES (1, 'USER', NOW(), NOW());
INSERT INTO roles (id, name, created_at, updated_at) VALUES (2, 'ADMIN', NOW(), NOW());

-- Insertar usuarios (password: 1234 encriptado con BCrypt)
-- BCrypt hash de "1234": $2a$10$NPCGGLu.aip7cmlCVbGRq.fBWDJacUxKoWPpDuWrMxUSsNEOM7pG.
INSERT INTO users (id, email, password, first_name, last_name, enabled, created_at, updated_at)
VALUES (1, 'user@test.com', '$2a$10$NPCGGLu.aip7cmlCVbGRq.fBWDJacUxKoWPpDuWrMxUSsNEOM7pG.', 'Test', 'User', true, NOW(), NOW());

INSERT INTO users (id, email, password, first_name, last_name, enabled, created_at, updated_at)
VALUES (2, 'admin@test.com', '$2a$10$NPCGGLu.aip7cmlCVbGRq.fBWDJacUxKoWPpDuWrMxUSsNEOM7pG.', 'Test', 'Admin', true, NOW(), NOW());

-- Asignar roles
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1); -- user@test.com -> ROLE_USER
INSERT INTO user_roles (user_id, role_id) VALUES (2, 1); -- admin@test.com -> ROLE_USER
INSERT INTO user_roles (user_id, role_id) VALUES (2, 2); -- admin@test.com -> ROLE_ADMIN

-- Insertar productos (10 productos con stock variado)
INSERT INTO products (id, name, description, price_amount, price_currency, stock, active, created_at, updated_at)
VALUES
    (1, 'Laptop Dell XPS 13', 'High-performance ultrabook', 1299.99, 'USD', 10, true, NOW(), NOW()),
    (2, 'iPhone 15 Pro', 'Latest Apple smartphone', 999.99, 'USD', 25, true, NOW(), NOW()),
    (3, 'Samsung Galaxy S24', 'Flagship Android phone', 899.99, 'USD', 15, true, NOW(), NOW()),
    (4, 'MacBook Pro 14"', 'Professional laptop', 1999.99, 'USD', 5, true, NOW(), NOW()),
    (5, 'Sony WH-1000XM5', 'Noise-cancelling headphones', 399.99, 'USD', 30, true, NOW(), NOW()),
    (6, 'iPad Air', 'Versatile tablet', 599.99, 'USD', 20, true, NOW(), NOW()),
    (7, 'Apple Watch Series 9', 'Smartwatch', 429.99, 'USD', 40, true, NOW(), NOW()),
    (8, 'Kindle Paperwhite', 'E-reader', 139.99, 'USD', 50, true, NOW(), NOW()),
    (9, 'Nintendo Switch', 'Gaming console', 299.99, 'USD', 0, true, NOW(), NOW()),
    (10, 'Discontinued Product', 'No longer available', 99.99, 'USD', 100, false, NOW(), NOW());
