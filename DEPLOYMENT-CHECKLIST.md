# ✅ Checklist de Deployment

Usa esta lista para trackear tu progreso. Marca cada checkbox a medida que completas los pasos.

---

## 📦 FASE 1: Preparar el Proyecto

### Archivos a copiar:

- [ ] `HealthController.java` → `src/main/java/com/ordersystem/api/health/`
- [ ] `application-prod.yml` → `src/main/resources/`
- [ ] Actualizar `SecurityConfig.java` (solo agregar 1 línea: `/api/health` público)
- [ ] `render.yaml` → raíz del proyecto
- [ ] `DEPLOYMENT.md` → raíz del proyecto
- [ ] `generate-jwt-secret.sh` → raíz del proyecto

### Validación local:

- [ ] Compilar proyecto: `./mvnw clean package`
- [ ] Verificar que todos los tests pasen: `./mvnw test`
- [ ] Dar permisos al script: `chmod +x generate-jwt-secret.sh`

### Git:

- [ ] Commit: `git add .`
- [ ] Commit: `git commit -m "feat: add production deployment configuration"`
- [ ] Push: `git push origin main`

---

## 🗄️ FASE 2: Configurar Neon (PostgreSQL)

- [ ] Crear cuenta en https://neon.tech (con GitHub)
- [ ] Crear proyecto "order-system"
- [ ] Seleccionar región (ej: US East)
- [ ] Copiar y guardar credenciales:

```bash
# Transformar URL de Neon a formato Spring Boot:
# De: postgresql://user:pass@host/db?ssl
# A:  jdbc:postgresql://host:5432/db?ssl

DATABASE_URL=jdbc:postgresql://ep-xxx.region.aws.neon.tech:5432/neondb?sslmode=require
DATABASE_USERNAME=tu_username
DATABASE_PASSWORD=tu_password
```

- [ ] **GUARDAR estos 3 valores** (los necesitarás en Render)

---

## 🔐 FASE 3: Generar JWT Secret

- [ ] Ejecutar: `./generate-jwt-secret.sh` (o `openssl rand -base64 32`)
- [ ] Copiar el JWT_SECRET generado
- [ ] **GUARDAR este valor** (lo necesitarás en Render)

Ejemplo:
```
JWT_SECRET=8xKfP2mN9vQ4wR5tY6uZ7aB8cD9eF0gH1iJ2kL3mN4oP
```

---

## 🌐 FASE 4: Configurar Render (Backend)

### Crear servicio:

- [ ] Crear cuenta en https://render.com (con GitHub)
- [ ] Click en "New +" → "Web Service"
- [ ] Conectar repositorio `order-system`

### Configuración:

- [ ] **Name**: `order-system`
- [ ] **Region**: Misma que Neon
- [ ] **Branch**: `main`
- [ ] **Runtime**: `Java`
- [ ] **Build Command**: `./mvnw clean package -DskipTests`
- [ ] **Start Command**: `java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/*.jar`
- [ ] **Plan**: `Free`

### Variables de Entorno:

Click en "Advanced" → "Add Environment Variable" y agrega:

- [ ] `SPRING_PROFILES_ACTIVE` = `prod`
- [ ] `DATABASE_URL` = (el valor de Neon con `jdbc:` y `:5432`)
- [ ] `DATABASE_USERNAME` = (el valor de Neon)
- [ ] `DATABASE_PASSWORD` = (el valor de Neon)
- [ ] `JWT_SECRET` = (el valor generado en Fase 3)
- [ ] `JAVA_TOOL_OPTIONS` = `-Xmx512m -Xms256m`

### Health Check:

- [ ] **Health Check Path**: `/api/health`

### Deploy:

- [ ] Click en "Create Web Service"
- [ ] Esperar 3-5 minutos (ver logs)
- [ ] Anotar tu URL: `https://order-system-xxxx.onrender.com`

---

## ✅ FASE 5: Verificación

Reemplaza `xxxx` con tu ID real de Render.

### Health Check:

- [ ] Abrir: `https://order-system-xxxx.onrender.com/api/health`
- [ ] Verificar respuesta: `{"status":"UP",...}`

### Swagger UI:

- [ ] Abrir: `https://order-system-xxxx.onrender.com/swagger-ui.html`
- [ ] Verificar que carga correctamente

### Registro de usuario:

- [ ] Ejecutar:
```bash
curl -X POST https://order-system-xxxx.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"Test123!","name":"Test User"}'
```
- [ ] Verificar respuesta exitosa

### Login:

- [ ] Ejecutar:
```bash
curl -X POST https://order-system-xxxx.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"Test123!"}'
```
- [ ] Copiar el token recibido

### Endpoint protegido:

- [ ] Ejecutar (con tu token):
```bash
curl -X GET https://order-system-xxxx.onrender.com/api/products \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```
- [ ] Verificar respuesta exitosa

---

## 📝 FASE 6: Documentación

### README:

- [ ] Agregar sección de deployment con tu URL real
- [ ] Agregar badge: `![Deploy](https://img.shields.io/badge/deploy-passing-brightgreen)`
- [ ] Agregar ejemplos de uso con tu URL de producción

### Ejemplo para README:

```markdown
## 🚀 Producción

**URL**: https://order-system-xxxx.onrender.com
**Swagger**: https://order-system-xxxx.onrender.com/swagger-ui.html

### Stack
- Backend: Render (Free Tier)
- Database: Neon PostgreSQL (Free Tier)
- Deploy: Automático desde GitHub
```

---

## 🎉 ¡Completado!

Si todos los checkboxes están marcados, ¡tu API está en producción!

### Deploy Automático

Cada push a `main` ahora automáticamente:
1. ✅ Detecta cambios (GitHub webhook)
2. ✅ Ejecuta build
3. ✅ Deploya nueva versión
4. ✅ Health check
5. ✅ Activa si OK

---

## 🐛 ¿Algo no funciona?

Consulta la sección **"Troubleshooting"** en `DEPLOYMENT.md`.

**Debug rápido:**
1. Verifica logs en Render: Dashboard → Logs
2. Verifica variables de entorno: Dashboard → Environment
3. Prueba health check: `curl https://tu-app.onrender.com/api/health`

---

## 📌 Notas

**Limitaciones del Free Tier:**
- ⚠️ App se suspende después de 15 min sin tráfico (cold start ~30s)
- ⚠️ DB se suspende después de 5 min sin actividad (cold start ~1-2s)
- ✅ Esto es normal y esperado para planes gratuitos

**Solución para cold starts:**
- Usar UptimeRobot (gratis) para hacer ping cada 5 minutos

---

**Tiempo total estimado**: 30-45 minutos  
**Dificultad**: 🟢 Fácil (bien documentado)

---
