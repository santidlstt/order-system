# 🛒 Order System - Sistema de Gestión de Pedidos

Sistema backend profesional para gestión de pedidos con autenticación JWT, construido con **Spring Boot 3** y **arquitectura limpia**.

[![CI Status](https://github.com/santidlstt/order-system/actions/workflows/ci.yml/badge.svg)](https://github.com/santidlstt/order-system/actions/workflows/ci.yml)
[![Deploy Status](https://img.shields.io/badge/deploy-passing-brightgreen)](https://order-system-wdqj.onrender.com)
[![Tests](https://img.shields.io/badge/tests-62%20passing-brightgreen)](https://github.com/santidlstt/order-system)
[![Coverage](https://img.shields.io/badge/coverage-60%25+-blue)](https://github.com/santidlstt/order-system)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.1-green?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Auth-red?logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker&logoColor=white)](https://www.docker.com/)

---

## 🌐 Producción

### 🚀 Aplicación Desplegada

**URL Base**: https://order-system-gsys.onrender.com 
**Swagger UI**: https://order-system-gsys.onrender.com/swagger-ui.html  
**Health Check**: https://order-system-gsys.onrender.com/api/health

### 📊 Stack de Producción

| Componente | Tecnología | Plan |
|------------|-----------|------|
| **Backend** | Render | Free Tier |
| **Base de Datos** | Neon PostgreSQL | Free Tier (3 GB) |
| **SSL/HTTPS** | Automático | ✅ Incluido |
| **Deploy** | GitHub Actions → Render | ✅ Automático |
| **Costo** | $0/mes | 100% Gratuito |

### ⚡ Características del Deploy

- ✅ **Deploy automático**: Push a `main` → Deploy automático
- ✅ **SSL/HTTPS**: Certificado automático y gratuito
- ✅ **Health checks**: Monitoreo automático cada 30s
- ✅ **PostgreSQL en la nube**: Base de datos gestionada en Neon
- ✅ **Migraciones automáticas**: Flyway se ejecuta en cada deploy
- ✅ **Variables de entorno seguras**: Secrets gestionados en Render

### 🧪 Probar la API en Producción

#### 1. Health Check
```bash
curl https://order-system-wdqj.onrender.com/api/health
```

**Respuesta esperada:**
```json
{
  "status": "UP",
  "timestamp": "2024-02-18T10:30:00",
  "service": "order-system",
  "version": "1.0.0"
}
```

#### 2. Login (Usuario de Prueba)
```bash
curl -X POST https://order-system-wdqj.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@test.com",
    "password": "1234"
  }'
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "email": "user@test.com",
  "roles": ["ROLE_USER"]
}
```

#### 3. Listar Productos (con token)
```bash
curl -X GET https://order-system-wdqj.onrender.com/api/products \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

#### 4. Crear Pedido (con token)
```bash
curl -X POST https://order-system-wdqj.onrender.com/api/orders \
  -H "Authorization: Bearer TU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {"productId": 1, "quantity": 2}
    ],
    "street": "Calle Falsa 123",
    "city": "Montevideo",
    "country": "Uruguay"
  }'
```

### ⚠️ Limitaciones del Free Tier

- **Cold Starts**: La aplicación se suspende después de 15 minutos sin tráfico. La primera request puede tardar ~30 segundos.
- **Base de Datos**: Neon free tier incluye 3 GB de almacenamiento y se suspende después de 5 minutos de inactividad (~1-2s de cold start).

> **Nota**: Estas limitaciones son esperadas para planes gratuitos y no afectan la funcionalidad de la demostración.

### 🔧 Arquitectura de Deployment

```
┌─────────────┐
│   GitHub    │ ← Código fuente (main branch)
│ order-system│
└──────┬──────┘
       │ Push → GitHub Webhook
       ↓
┌─────────────┐
│   Render    │ ← Build automático
│  Docker VM  │    - Maven compile
└──────┬──────┘    - Run tests (opcional)
       │            - Package JAR
       ↓
┌─────────────┐
│   Render    │ ← Servidor de Producción
│ Web Service │    - Java 17 + Spring Boot
└──────┬──────┘    - Puerto 10000
       │            - Health checks
       ↓
┌─────────────┐
│    Neon     │ ← Base de Datos PostgreSQL
│  PostgreSQL │    - 3 GB storage
└─────────────┘    - Migraciones automáticas (Flyway)
```

### 📝 Variables de Entorno (Producción)

Las siguientes variables están configuradas en Render:

```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://[neon-host]:5432/neondb?sslmode=require
DATABASE_USERNAME=***
DATABASE_PASSWORD=***
JWT_SECRET=***
JAVA_TOOL_OPTIONS=-Xmx512m -Xms256m
```

### 📖 Guía de Deployment

Para instrucciones detalladas sobre cómo se realizó el deployment, consulta:
- [DEPLOYMENT.md](./DEPLOYMENT.md) - Guía completa paso a paso
- [CHECKLIST.md](./CHECKLIST.md) - Checklist de deployment

---

## 🚀 Características

- ✅ **Autenticación JWT** con roles (USER, ADMIN)
- ✅ **Gestión de productos** (CRUD completo con soft delete)
- ✅ **Sistema de pedidos** con validación de stock
- ✅ **Pagos simulados** (idempotentes)
- ✅ **Gestión de stock** automática (descuenta al pagar, devuelve al cancelar)
- ✅ **Manejo de errores** profesional con excepciones custom
- ✅ **Clean Architecture** (Hexagonal)
- ✅ **Testing robusto** (62 tests: 19 unitarios + 43 integración)
- ✅ **CI/CD** con GitHub Actions
- ✅ **Deploy automático** a Render
- ✅ **Documentación Swagger/OpenAPI**
- ✅ **Migraciones con Flyway**
- ✅ **Docker Compose** para desarrollo local

## 🏗️ Arquitectura
```
order-system/
├── api/              # Controllers, DTOs y manejo de excepciones
├── application/      # Casos de uso / Servicios
├── domain/           # Modelos de negocio + excepciones custom
└── infrastructure/   # Persistencia, seguridad, configuración
```

**Principios aplicados:**
- Separación de capas (Clean Architecture)
- Repository Pattern
- DTO Pattern
- Value Objects
- Exception-driven design
- Códigos HTTP semánticos

## 🛠️ Tecnologías

| Categoría | Tecnología |
|-----------|-----------|
| **Backend** | Java 17, Spring Boot 3.4.1 |
| **Base de datos** | PostgreSQL 15 (producción), H2 (tests) |
| **Seguridad** | Spring Security, JWT (jjwt 0.12.3) |
| **ORM** | JPA / Hibernate |
| **Migraciones** | Flyway |
| **Testing** | JUnit 5, Mockito, MockMvc, Spring Test |
| **Code Coverage** | JaCoCo (60%+) |
| **CI/CD** | GitHub Actions + Render |
| **Cloud** | Render (backend), Neon (PostgreSQL) |
| **Documentación** | SpringDoc OpenAPI 3 |
| **Build** | Maven |
| **Contenedores** | Docker, Docker Compose |

## 📋 Requisitos Previos

- **Java 17** o superior
- **Maven 3.8+**
- **Docker** y **Docker Compose**
- **Git**

## 🚀 Instalación y Ejecución (Desarrollo Local)

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
./mvnw clean install
```

### 4. Ejecutar la aplicación
```bash
./mvnw spring-boot:run
```

O desde tu IDE favorito ejecutando `OrderSystemApplication.java`

### 5. Acceder a Swagger (Local)
```
http://localhost:8080/swagger-ui.html
```

## 🧪 Testing

### Ejecutar todos los tests (62 tests)
```bash
./mvnw verify
```

Este comando ejecuta:
- Tests unitarios (Maven Surefire)
- Tests de integración (Maven Failsafe)
- Genera reporte de cobertura con JaCoCo

### Ejecutar solo tests unitarios (19 tests)
```bash
./mvnw test
```

### Ejecutar solo tests de integración (43 tests)
```bash
./mvnw integration-test
```

### Ver reporte de cobertura
```bash
# Después de ejecutar ./mvnw verify
# Linux/Mac
open target/site/jacoco/index.html

# Windows
start target/site/jacoco/index.html
```

### Cobertura de Tests

- ✅ **Tests Unitarios** (19 tests):
    - CreateOrderServiceTest (5 tests)
    - PayOrderServiceTest (7 tests)
    - CancelOrderServiceTest (7 tests)

- ✅ **Tests de Integración** (43 tests):
    - AuthControllerIntegrationTest (7 tests)
    - OrderControllerIntegrationTest (36 tests)
        - Crear pedidos con validaciones
        - Gestión de permisos (USER/ADMIN)
        - Flujos completos (crear → pagar → cancelar)
        - Validación de stock y estados

- ✅ **Cobertura de Código**: 60%+ (líneas), 50%+ (branches)

## 🔐 Usuarios de Prueba

| Email | Password | Rol | Disponible en |
|-------|----------|-----|---------------|
| `user@test.com` | `1234` | USER | Local + Producción |
| `admin@test.com` | `1234` | ADMIN | Local + Producción |

## 📚 API Reference

### Autenticación
```http
POST /api/auth/login
```
**Body:**
```json
{
  "email": "user@test.com",
  "password": "1234"
}
```
**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "email": "user@test.com",
  "roles": ["ROLE_USER"]
}
```

### Productos (Endpoints Públicos para GET)
```http
GET    /api/products           # Listar productos
GET    /api/products/{id}      # Ver producto
GET    /api/products/active    # Listar solo activos
POST   /api/products           # Crear (ADMIN)
PUT    /api/products/{id}      # Actualizar (ADMIN)
DELETE /api/products/{id}      # Soft delete (ADMIN)
```

### Pedidos (Requieren Autenticación)
```http
POST /api/orders                # Crear pedido (USER/ADMIN)
GET  /api/orders                # Listar pedidos
GET  /api/orders/{id}           # Ver detalle
POST /api/orders/{id}/pay       # Pagar (descuenta stock)
POST /api/orders/{id}/cancel    # Cancelar (devuelve stock)
PUT  /api/orders/{id}/ship      # Marcar enviado (ADMIN)
```

## 🎯 Flujo de Prueba Completo (Local)

### 1. Login como usuario
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@test.com",
    "password": "1234"
  }'
```

### 2. Crear pedido
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <tu-token-aqui>" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {"productId": 1, "quantity": 2}
    ],
    "street": "Calle Falsa 123",
    "city": "Montevideo",
    "country": "Uruguay"
  }'
```

### 3. Pagar pedido (descuenta stock)
```bash
curl -X POST http://localhost:8080/api/orders/1/pay \
  -H "Authorization: Bearer <tu-token-aqui>" \
  -H "Content-Type: application/json" \
  -d '{
    "paymentMethod": "credit_card"
  }'
```

### 4. Cancelar pedido (devuelve stock)
```bash
curl -X POST http://localhost:8080/api/orders/1/cancel \
  -H "Authorization: Bearer <tu-token-aqui>"
```

## 🚨 Manejo de Errores

El sistema retorna códigos HTTP semánticos y mensajes claros:

| Código | Descripción | Ejemplo |
|--------|-------------|---------|
| `200` | Operación exitosa | Pedido pagado |
| `201` | Recurso creado | Pedido creado |
| `400` | Validación fallida | Stock insuficiente, producto inactivo |
| `401` | No autenticado | Token inválido o ausente |
| `403` | Sin permisos | USER intentando acceder a pedido ajeno |
| `404` | No encontrado | Producto o pedido inexistente |
| `409` | Conflicto de estado | Intentar pagar pedido cancelado |
| `500` | Error interno | Error inesperado del servidor |

**Ejemplo de respuesta de error:**
```json
{
  "timestamp": "2025-02-16T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Stock insuficiente para el producto 'iPhone 15'. Solicitado: 5, Disponible: 2",
  "path": "/api/orders"
}
```

## 🗄️ Base de Datos

### Desarrollo Local - Acceder a pgAdmin

1. Abrir: `http://localhost:5050`
2. Login: `admin@ordersystem.com` / `admin`
3. Conectar servidor:
    - Host: `postgres`
    - Port: `5432`
    - Usuario: `orderuser`
    - Password: `orderpass`

### Migraciones Flyway

Las migraciones se ejecutan automáticamente al iniciar (local y producción):

- **V1**: Users y Roles
- **V2**: Products
- **V3**: Orders y Order Items
- **V4**: Payments

## 🔧 Configuración

### Variables de Entorno (Desarrollo Local)
```bash
JWT_SECRET=tu-secret-super-seguro-aqui
JWT_EXPIRATION=1800000
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/orderdb
SPRING_DATASOURCE_USERNAME=orderuser
SPRING_DATASOURCE_PASSWORD=orderpass
```

### Perfiles de Spring
```bash
# Desarrollo (H2 para tests)
./mvnw test

# Desarrollo (PostgreSQL local)
./mvnw spring-boot:run

# Producción (usa application-prod.yml)
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## 📦 Estructura del Proyecto
```
src/
├── main/
│   ├── java/com/ordersystem/
│   │   ├── api/
│   │   │   ├── auth/                 # AuthController
│   │   │   ├── health/               # HealthController (producción)
│   │   │   ├── order/                # OrderController + DTOs
│   │   │   ├── product/              # ProductController + DTOs
│   │   │   └── exception/            # GlobalExceptionHandler
│   │   ├── application/              # Services (casos de uso)
│   │   ├── domain/
│   │   │   ├── order/                # Order, OrderItem, OrderStatus
│   │   │   ├── product/              # Product
│   │   │   ├── user/                 # User, Role
│   │   │   └── exception/            # Excepciones custom (5)
│   │   └── infrastructure/
│   │       ├── persistence/          # JPA Entities + Repositories
│   │       ├── security/             # JWT, SecurityConfig
│   │       └── mapper/               # Entity ↔ Domain mappers
│   └── resources/
│       ├── application.yml           # Configuración desarrollo
│       ├── application-prod.yml      # Configuración producción
│       └── db/migration/             # Scripts Flyway
├── Dockerfile                        # Imagen Docker para producción
├── start.sh                          # Script de inicio (producción)
└── test/
    ├── java/com/ordersystem/
    │   ├── api/                      # Tests de integración (43)
    │   │   ├── BaseIntegrationTest
    │   │   ├── auth/                 # AuthController tests
    │   │   └── order/                # OrderController tests
    │   └── application/              # Tests unitarios (19)
    └── resources/
        ├── application-test.yml      # Config H2
        └── test-data.sql             # Datos de prueba
```

## 🐳 Docker

### Detener contenedores (local)
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

## 🔄 CI/CD

Este proyecto implementa **integración y entrega continua** completa:

### GitHub Actions (CI)
- ✅ **Build automático** en cada push y pull request
- ✅ **62 tests** ejecutados automáticamente (unitarios + integración)
- ✅ **Análisis de cobertura** con JaCoCo (60%+ requerido)
- ✅ **Reportes descargables** como artifacts
- ✅ **Cache de dependencias** Maven (builds ~2 minutos)

### Render (CD)
- ✅ **Deploy automático** en cada push a `main`
- ✅ **Docker build** automático
- ✅ **Health checks** después del deploy
- ✅ **Rollback automático** si falla el health check
- ✅ **Zero downtime** durante deploys

**Ver workflow CI:** [.github/workflows/ci.yml](.github/workflows/ci.yml)

**Estado del CI/CD:**
- [![CI Status](https://github.com/santidlstt/order-system/actions/workflows/ci.yml/badge.svg)](https://github.com/santidlstt/order-system/actions/workflows/ci.yml)
- [![Deploy Status](https://img.shields.io/badge/deploy-passing-brightgreen)](https://order-system-wdqj.onrender.com)

## 📈 Roadmap

### ✅ Completado
- [x] Sistema de pedidos completo
- [x] Autenticación JWT
- [x] Gestión automática de stock
- [x] Tests unitarios e integración (62 tests)
- [x] Manejo de excepciones profesional
- [x] Documentación Swagger
- [x] CI/CD con GitHub Actions
- [x] Code coverage con JaCoCo
- [x] **Deploy a producción (Render + Neon)**
- [x] **Deploy automático desde GitHub**
- [x] **SSL/HTTPS en producción**

### 🚧 En Progreso
- [ ] Aumentar cobertura a 80%+

### 🔮 Futuro
- [ ] Paginación en listados
- [ ] Filtros y búsqueda avanzada
- [ ] Integración con pasarela de pago real
- [ ] Sistema de notificaciones
- [ ] Rate limiting
- [ ] Reportes y estadísticas
- [ ] WebSockets para notificaciones en tiempo real
- [ ] Monitoreo con Prometheus/Grafana

## 👨‍💻 Autor

**Santiago de los Santos**
- GitHub: [@santidlstt](https://github.com/santidlstt)
- LinkedIn: [Santiago de los Santos](https://www.linkedin.com/in/santiago-de-los-santos-8a2a3a337/)

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la licencia MIT.

---

⭐ **Si este proyecto te fue útil, considera darle una estrella en GitHub**

💬 **¿Preguntas o sugerencias?** Abre un [issue](https://github.com/santidlstt/order-system/issues)

🌐 **Prueba la API en vivo**: https://order-system-gsys.onrender.com/swagger-ui.html