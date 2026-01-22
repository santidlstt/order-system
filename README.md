# 🛒 Order System - Sistema de Gestión de Pedidos

Sistema backend profesional para gestión de pedidos con autenticación JWT, construido con **Spring Boot 3** y **arquitectura limpia**.

## 🚀 Características

- ✅ **Autenticación JWT** con roles (USER, ADMIN)
- ✅ **Gestión de productos** (CRUD completo)
- ✅ **Sistema de pedidos** con validación de stock
- ✅ **Pagos simulados** (idempotentes)
- ✅ **Gestión de stock** automática
- ✅ **Clean Architecture** (Hexagonal)
- ✅ **Documentación Swagger/OpenAPI**
- ✅ **Migraciones con Flyway**
- ✅ **Docker Compose** para desarrollo local

## 🏗️ Arquitectura

```
order-system/
├── api/              # Controllers y DTOs
├── application/      # Casos de uso / Servicios
├── domain/           # Modelos de negocio (sin dependencias)
└── infrastructure/   # Persistencia, seguridad, configuración
```

**Principios aplicados:**
- Separación de capas (Clean Architecture)
- Repository Pattern
- DTO Pattern
- Value Objects
- Transaction Script

## 🛠️ Tecnologías

| Categoría | Tecnología |
|-----------|-----------|
| **Backend** | Java 17, Spring Boot 3.4.1 |
| **Base de datos** | PostgreSQL 15 |
| **Seguridad** | Spring Security, JWT (jjwt 0.12.3) |
| **ORM** | JPA / Hibernate |
| **Migraciones** | Flyway |
| **Documentación** | SpringDoc OpenAPI 3 |
| **Build** | Maven |
| **Contenedores** | Docker, Docker Compose |

## 📋 Requisitos Previos

- **Java 17** o superior
- **Maven 3.8+**
- **Docker** y **Docker Compose**
- **Git**

## 🚀 Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/santidlstt/order-system.git
cd order-system
```

### 2. Levantar PostgreSQL con Docker

```bash
docker compose up -d
```

Esto iniciará:
- PostgreSQL en `localhost:5433`
- pgAdmin en `http://localhost:5050`

### 3. Compilar el proyecto

```bash
mvn clean install
```

### 4. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

O desde tu IDE favorito ejecutando `OrderSystemApplication.java`

### 5. Acceder a Swagger

```
http://localhost:8080/swagger-ui.html
```

## 🔐 Usuarios de Prueba

| Email | Password | Rol |
|-------|----------|-----|
| `user@test.com` | `1234` | USER |
| `admin@test.com` | `1234` | ADMIN |

## 📚 Endpoints Principales

### Autenticación
```
POST /api/auth/login - Login (devuelve JWT)
```

### Productos
```
GET    /api/products        - Listar productos (público)
GET    /api/products/{id}   - Ver producto (público)
POST   /api/products        - Crear producto (ADMIN)
PUT    /api/products/{id}   - Actualizar producto (ADMIN)
DELETE /api/products/{id}   - Soft delete producto (ADMIN)
```

### Pedidos
```
POST /api/orders             - Crear pedido (USER)
GET  /api/orders             - Listar pedidos (USER: propios, ADMIN: todos)
GET  /api/orders/{id}        - Ver detalle del pedido
POST /api/orders/{id}/pay    - Pagar pedido (descuenta stock)
POST /api/orders/{id}/cancel - Cancelar pedido (devuelve stock)
PUT  /api/orders/{id}/ship   - Marcar como enviado (ADMIN)
```

## 🧪 Flujo de Prueba Completo

### 1. Login
```bash
POST /api/auth/login
{
  "email": "user@test.com",
  "password": "1234"
}
```

### 2. Crear pedido
```bash
POST /api/orders
Authorization: Bearer <token>
{
  "items": [
    {"productId": 1, "quantity": 2}
  ],
  "street": "Calle Falsa 123",
  "city": "Montevideo",
  "country": "Uruguay"
}
```

### 3. Pagar pedido
```bash
POST /api/orders/1/pay
{
  "paymentMethod": "credit_card"
}
```

## 🗄️ Base de Datos

### Acceder a pgAdmin

1. Abrir: `http://localhost:5050`
2. Login: `admin@ordersystem.com` / `admin`
3. Conectar servidor:
    - Host: `postgres`
    - Port: `5432`
    - Usuario: `orderuser`
    - Password: `orderpass`

### Migraciones Flyway

Las migraciones se ejecutan automáticamente al iniciar:

- **V1**: Users y Roles
- **V2**: Products
- **V3**: Orders y Order Items
- **V4**: Payments

## 🔧 Configuración

### Variables de Entorno (Producción)

```bash
JWT_SECRET=tu-secret-super-seguro-aqui
JWT_EXPIRATION=1800000
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/orderdb
SPRING_DATASOURCE_USERNAME=orderuser
SPRING_DATASOURCE_PASSWORD=orderpass
```

### Perfiles de Spring

```bash
# Desarrollo
mvn spring-boot:run

# Producción (requiere application-prod.yml)
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## 📦 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/ordersystem/
│   │   ├── api/              # REST Controllers
│   │   ├── application/      # Services (casos de uso)
│   │   ├── domain/           # Modelos de dominio
│   │   └── infrastructure/   # JPA, Security, Config
│   └── resources/
│       ├── application.yml
│       └── db/migration/     # Scripts Flyway
└── test/
    └── java/                 # Tests unitarios
```

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar un test específico
mvn test -Dtest=CreateOrderServiceTest
```

## 🐳 Docker

### Detener contenedores
```bash
docker compose down
```

### Recrear base de datos limpia
```bash
docker compose down -v
docker compose up -d
```

### Ver logs
```bash
docker logs orderdb
docker logs pgadmin
```

## 📈 Próximas Mejoras

- [ ] Tests de integración completos
- [ ] CI/CD con GitHub Actions
- [ ] Deploy a cloud (Render/Railway)
- [ ] Integración con pasarela de pago real
- [ ] Sistema de notificaciones
- [ ] Reportes y estadísticas

## 👨‍💻 Autor

**Tu Nombre**
- GitHub: [@santidlstt](https://github.com/santidlstt)
- LinkedIn: [Santiago de los Santos](https://www.linkedin.com/in/santiago-de-los-santos-8a2a3a337/)

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

---

⭐ Si este proyecto te fue útil, considera darle una estrella en GitHub