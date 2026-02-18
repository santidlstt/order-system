# Dockerfile para Order System
# Usa una imagen base de Java 17

FROM eclipse-temurin:17-jdk-alpine AS build

# Directorio de trabajo
WORKDIR /app

# Copiar Maven Wrapper y pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Dar permisos de ejecución al Maven Wrapper
RUN chmod +x mvnw

# Descargar dependencias (cache layer)
RUN ./mvnw dependency:go-offline

# Copiar código fuente
COPY src ./src

# Compilar la aplicación (sin tests para deploy más rápido)
RUN ./mvnw clean package -DskipTests

# Etapa final - imagen más pequeña
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Copiar script de inicio
COPY start.sh start.sh
RUN chmod +x start.sh

# Exponer el puerto
EXPOSE 8080

# Usar el script de inicio
CMD ["./start.sh"]
