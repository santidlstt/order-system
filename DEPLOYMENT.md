# 🚀 Guía de Deployment a Producción

Esta guía te llevará paso a paso para deployar el Order System a producción usando **Render** (backend) + **Neon** (PostgreSQL).

**Stack 100% gratuito:**
- Backend: Render Free Tier
- Base de datos: Neon PostgreSQL Free Tier
- Deploy automático desde GitHub

---

## 📋 Pre-requisitos

- [ ] Cuenta de GitHub (ya tienes el repo)
- [ ] Cuenta de Neon (PostgreSQL) - https://neon.tech
- [ ] Cuenta de Render (Web Service) - https://render.com
- [ ] 30-45 minutos de tiempo

---

## 🗄️ PARTE 1: Configurar Neon (Base de Datos PostgreSQL)

### Paso 1: Crear cuenta y proyecto

1. Ve a https://neon.tech
2. Regístrate con GitHub (recomendado, más rápido)
3. Haz clic en "Create a project"

### Paso 2: Configuración del proyecto

Completa los datos:
- **Project name**: `order-system`
- **Region**: Selecciona el más cercano a ti (ej: US East para mejor latencia con Render)
- **PostgreSQL version**: 15 o 16 (ambas funcionan)

Haz clic en "Create Project"

### Paso 3: Obtener credenciales de conexión

Neon te mostrará una pantalla con la connection string. **¡COPIA Y GUARDA ESTA INFORMACIÓN!**

Verás algo como:
```
postgresql://username:password@ep-cool-name-123456.us-east-2.aws.neon.tech/neondb?sslmode=require
```

**IMPORTANTE: Necesitas transformar esta URL para Spring Boot:**

**Formato que te da Neon:**
```
postgresql://tu_user:tu_pass@ep-xxx-123456.region.aws.neon.tech/neondb?sslmode=require
```

**Formato que necesitas para Render (3 variables):**
```bash
DATABASE_URL=jdbc:postgresql://ep-xxx-123456.region.aws.neon.tech:5432/neondb?sslmode=require
DATABASE_USERNAME=tu_user
DATABASE_PASSWORD=tu_pass
```

**Cambios a realizar:**
1. Agregar `jdbc:` antes de `postgresql://`
2. Agregar `:5432` después del host (puerto)
3. Extraer el username (antes de `:`)
4. Extraer el password (entre `:` y `@`)

### Paso 4: Probar conexión (opcional)

Si quieres verificar que las credenciales funcionan localmente:

```bash
# Crea un archivo .env temporal (NO SUBIR A GIT)
export DATABASE_URL="jdbc:postgresql://..."
export DATABASE_USERNAME="tu_username"
export DATABASE_PASSWORD="tu_password"
export JWT_SECRET="cualquier-valor-temporal"

# Ejecuta la app con perfil prod
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

Si todo funciona, verás que Flyway ejecuta las migraciones.

---

## 🔐 PARTE 2: Generar JWT Secret

### Generar secret seguro (256 bits)

**Opción 1 - Linux/Mac/Git Bash:**
```bash
chmod +x generate-jwt-secret.sh
./generate-jwt-secret.sh
```

**Opción 2 - Comando directo:**
```bash
openssl rand -base64 32
```

**Opción 3 - Windows PowerShell:**
```powershell
-join ((65..90) + (97..122) + (48..57) | Get-Random -Count 32 | ForEach-Object {[char]$_})
```

**Ejemplo de output:**
```
8xKfP2mN9vQ4wR5tY6uZ7aB8cD9eF0gH1iJ2kL3mN4oP
```

> ⚠️ **GUARDA ESTE VALOR**: Lo necesitarás en el siguiente paso

---

## 🌐 PARTE 3: Configurar Render (Backend)

### Paso 1: Crear cuenta

1. Ve a https://render.com
2. Haz clic en "Get Started"
3. Regístrate con GitHub (recomendado)
4. Autoriza a Render para acceder a tus repositorios

### Paso 2: Crear Web Service

1. En el dashboard, haz clic en **"New +"** → **"Web Service"**
2. Conecta tu repositorio:
    - Busca `order-system`
    - Si no aparece, haz clic en "Configure account" para dar permisos
    - Haz clic en "Connect"

### Paso 3: Configurar el servicio

Completa el formulario:

| Campo | Valor |
|-------|-------|
| **Name** | `order-system` (o el nombre que prefieras) |
| **Region** | Misma que Neon (ej: Oregon si Neon está en US West) |
| **Branch** | `main` |
| **Runtime** | `Java` |
| **Build Command** | `./mvnw clean package -DskipTests` |
| **Start Command** | `java -Dserver.port=$PORT -Dspring.profiles.active=prod -jar target/*.jar` |
| **Instance Type** | `Free` |

### Paso 4: Configurar Variables de Entorno

Haz clic en **"Advanced"** y luego en **"Add Environment Variable"**.

Agrega estas 5 variables (copia los valores que guardaste antes):

```bash
# 1. Perfil de Spring
SPRING_PROFILES_ACTIVE=prod

# 2. URL de la base de datos (desde Neon, recuerda agregar jdbc: y :5432)
DATABASE_URL=jdbc:postgresql://ep-xxx-123456.region.aws.neon.tech:5432/neondb?sslmode=require

# 3. Usuario de la base de datos (desde Neon)
DATABASE_USERNAME=tu_neon_username

# 4. Contraseña de la base de datos (desde Neon)
DATABASE_PASSWORD=tu_neon_password

# 5. JWT Secret (generado en Parte 2)
JWT_SECRET=tu_jwt_secret_de_32_caracteres

# 6. Opciones de Java (limita memoria para free tier)
JAVA_TOOL_OPTIONS=-Xmx512m -Xms256m
```

> 💡 **Tip**: Copia y pega los valores reales, no estos placeholders

### Paso 5: Configurar Health Check (opcional pero recomendado)

En la sección **"Health Check"**:
- **Health Check Path**: `/api/health`

### Paso 6: ¡Deploy!

1. Haz clic en **"Create Web Service"**
2. Render empezará a buildear tu aplicación automáticamente
3. Verás los logs en tiempo real
4. Espera 3-5 minutos (el primer deploy es más lento)

**Logs esperados (si todo va bien):**
```
[INFO] Building order-system 0.0.1-SNAPSHOT
[INFO] Running Flyway migrations...
[INFO] Successfully applied 4 migrations
[INFO] Tomcat started on port 10000
[INFO] Started OrderSystemApplication in 45.2 seconds
```

**Tu URL será algo como:**
```
https://order-system-xxxx.onrender.com
```

---

## ✅ PARTE 4: Verificación

### 1. Probar Health Check

```bash
curl https://order-system-xxxx.onrender.com/api/health
```

**Respuesta esperada:**
```json
{
  "status": "UP",
  "timestamp": "2024-02-17T10:30:00",
  "service": "order-system",
  "version": "1.0.0"
}
```

### 2. Probar Swagger UI

Abre en tu navegador:
```
https://order-system-xxxx.onrender.com/swagger-ui.html
```

Deberías ver la documentación completa de la API.

### 3. Probar Registro de Usuario

```bash
curl -X POST https://order-system-xxxx.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123!",
    "name": "Usuario Prueba"
  }'
```

### 4. Probar Login

```bash
curl -X POST https://order-system-xxxx.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123!"
  }'
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "email": "test@example.com",
  "roles": ["ROLE_USER"]
}
```

### 5. Probar Endpoint Protegido

```bash
# Guarda el token del paso anterior
TOKEN="tu_token_aqui"

# Listar productos
curl -X GET https://order-system-xxxx.onrender.com/api/products \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🔄 PARTE 5: Deploy Automático

### ¡Ya está configurado! 🎉

Render ya está escuchando cambios en tu rama `main`. Cada vez que hagas push:

```bash
git add .
git commit -m "feat: nueva funcionalidad"
git push origin main
```

**Render automáticamente:**
1. ✅ Detecta el push (webhook de GitHub)
2. ✅ Clona el código actualizado
3. ✅ Ejecuta el build (`./mvnw clean package`)
4. ✅ Deploya la nueva versión
5. ✅ Hace health check
6. ✅ Si OK, activa la nueva versión

Puedes ver el progreso en tiempo real en la pestaña **"Logs"** de Render.

---

## 📊 Monitoreo y Logs

### Ver logs en tiempo real

1. Ve a tu servicio en Render
2. Haz clic en la pestaña **"Logs"**
3. Selecciona **"Live logs"** para ver en tiempo real

### Comandos útiles

```bash
# Ver salud del servicio
curl https://tu-app.onrender.com/api/health

# Registrar usuario de prueba
curl -X POST https://tu-app.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"Test123!","name":"Test"}'
```

---

## ⚠️ Limitaciones del Free Tier

### Render Free Tier
- ✅ 750 horas/mes (suficiente para 1 app 24/7)
- ⚠️ La app se suspende después de 15 minutos sin tráfico
- ⚠️ Primera request después de suspensión: ~30 segundos (cold start)
- ✅ 100 GB de ancho de banda/mes
- ✅ SSL/HTTPS automático

### Neon Free Tier
- ✅ 3 GB de almacenamiento
- ✅ 10 proyectos
- ⚠️ Base de datos se suspende después de 5 minutos sin actividad
- ⚠️ Cold start: ~1-2 segundos
- ✅ Sin límite de queries

### ¿Cómo evitar cold starts?

**Opción 1: UptimeRobot (gratis)**
- Hace ping a tu app cada 5 minutos
- Mantiene la app "despierta"
- URL: https://uptimerobot.com

**Opción 2: Cron job en GitHub Actions**
```yaml
# .github/workflows/keep-alive.yml
# Hace ping cada 14 minutos
```

---

## 🐛 Troubleshooting

### ❌ Error: "Build failed"

**Causa 1: Falta Maven Wrapper**
```bash
# Solución: Asegúrate de que mvnw esté en Git
git add mvnw mvnw.cmd .mvn/
git commit -m "add maven wrapper"
git push
```

**Causa 2: Tests fallan en build**
```bash
# Solución temporal: Usa -DskipTests en build command
# Solución correcta: Arregla los tests que fallan localmente
```

### ❌ Error: "Application failed to start"

**Causa 1: Variables de entorno mal configuradas**
- Verifica que `DATABASE_URL` tenga el formato correcto: `jdbc:postgresql://...`
- Verifica que `JWT_SECRET` esté configurado
- Revisa los logs en Render

**Causa 2: Flyway no puede migrar**
```
Error: Flyway migration failed
```
- Verifica que la DB de Neon esté activa
- Asegúrate de que las credenciales sean correctas

### ❌ Error: "Connection timeout"

**Causa: Neon DB suspendida (cold start)**
- Espera 1-2 segundos y reintenta
- Es normal en el primer request después de inactividad

### ❌ App se queda en "Starting..."

**Causa 1: Health check path incorrecto**
- Verifica que `/api/health` sea accesible
- Puedes desactivar health check temporalmente para debug

**Causa 2: Puerto incorrecto**
- Verifica que el start command incluya `-Dserver.port=$PORT`

---

## 📝 Actualizar README

Una vez deployado, actualiza tu README.md con:

```markdown
## 🚀 Deploy en Producción

**URL de producción**: https://order-system-xxxx.onrender.com
**Swagger UI**: https://order-system-xxxx.onrender.com/swagger-ui.html

### Stack
- Backend: Render (Free Tier)
- Base de datos: Neon PostgreSQL (Free Tier)
- CI/CD: GitHub Actions + Render auto-deploy
```

---

## 🎉 ¡Felicidades!

Tu API REST está en producción con:

✅ URL pública funcional  
✅ Deploy automático desde GitHub  
✅ Base de datos PostgreSQL en la nube  
✅ SSL/HTTPS gratis  
✅ Swagger UI accesible  
✅ Costo total: $0/mes

---

## 🆘 ¿Necesitas Ayuda?

**Comandos de debug útiles:**

```bash
# 1. Verificar health
curl https://tu-app.onrender.com/api/health

# 2. Ver logs en Render
Dashboard → Tu servicio → Logs

# 3. Verificar variables de entorno
Dashboard → Tu servicio → Environment

# 4. Re-deployar manualmente
Dashboard → Tu servicio → Manual Deploy → Deploy latest commit
```

**Recursos:**
- Documentación de Render: https://render.com/docs
- Documentación de Neon: https://neon.tech/docs
- Tu repositorio: https://github.com/santidlstt/order-system

---

**Creado**: 2024-02-17  
**Última actualización**: 2024-02-17