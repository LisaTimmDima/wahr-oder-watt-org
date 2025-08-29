# Stage 1: build frontend
FROM node:20 AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ .
RUN npm run build

# Stage 2: build backend (Spring Boot with frontend included)
FROM maven:3.9.8-eclipse-temurin-21-alpine AS backend-build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
# копируем фронтенд билды в static
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN mvn clean package -DskipTests

# Stage 3: runtime
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java","-jar","app.jar"]
