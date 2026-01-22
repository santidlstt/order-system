-- =====================================================
-- V1: Crear tablas de usuarios y roles
-- =====================================================

-- Tabla de roles
CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de usuarios
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       enabled BOOLEAN NOT NULL DEFAULT true,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabla intermedia para relación muchos a muchos
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Índices para mejorar performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- =====================================================
-- Datos iniciales: Roles
-- =====================================================
INSERT INTO roles (name) VALUES ('USER');
INSERT INTO roles (name) VALUES ('ADMIN');

-- =====================================================
-- Datos iniciales: Usuarios de prueba
-- Contraseña para ambos: "1234" (hasheada con BCrypt)
-- =====================================================

-- Usuario regular: user@test.com / 1234
INSERT INTO users (email, password, first_name, last_name, enabled)
VALUES (
           'user@test.com',
           '$2a$10$JhTwk2qVf3/.SyuSi7rnCO.L.9LdM91OCesTSTRBp8NyjjfqxVuc6',
           'Test',
           'User',
           true
       );

-- Usuario administrador: admin@test.com / 1234
INSERT INTO users (email, password, first_name, last_name, enabled)
VALUES (
           'admin@test.com',
           '$2a$10$JhTwk2qVf3/.SyuSi7rnCO.L.9LdM91OCesTSTRBp8NyjjfqxVuc6',
           'Admin',
           'User',
           true
       );

-- =====================================================
-- Asignar roles a usuarios
-- =====================================================

-- user@test.com tiene rol USER
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'user@test.com' AND r.name = 'USER';

-- admin@test.com tiene rol ADMIN
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@test.com' AND r.name = 'ADMIN';