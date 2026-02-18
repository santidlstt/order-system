#!/bin/sh

# Script de inicio para Order System
# Este script asegura que todas las variables de entorno se pasen correctamente a Java

echo "==> Iniciando Order System..."
echo "==> Perfil activo: ${SPRING_PROFILES_ACTIVE}"
echo "==> Puerto: ${PORT}"
echo "==> Variables de entorno configuradas:"
echo "DATABASE_URL: ${DATABASE_URL:0:50}..." # Solo primeros 50 caracteres por seguridad
echo "DATABASE_USERNAME: ${DATABASE_USERNAME}"
echo "JWT_SECRET configurado: $([ -n "$JWT_SECRET" ] && echo 'SI' || echo 'NO')"

# Ejecutar Java con todas las variables explícitamente
exec java \
  -Dspring.profiles.active="${SPRING_PROFILES_ACTIVE}" \
  -Dserver.port="${PORT}" \
  -Dspring.datasource.url="${DATABASE_URL}" \
  -Dspring.datasource.username="${DATABASE_USERNAME}" \
  -Dspring.datasource.password="${DATABASE_PASSWORD}" \
  -Djwt.secret="${JWT_SECRET}" \
  -Xmx512m \
  -Xms256m \
  -jar app.jar