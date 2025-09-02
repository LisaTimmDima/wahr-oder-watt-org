# -------------------------
# Stage 1: Build Frontend
# -------------------------
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ .
RUN npm run build   

# -------------------------
# Stage 2: Build Backend
# -------------------------
FROM maven:3.9.11-eclipse-temurin-21-alpine AS backend-build
WORKDIR /app/backend
COPY backend/pom.xml ./
RUN mvn dependency:go-offline
COPY backend/ .
RUN mvn package -DskipTests

# -------------------------
# Stage 3: Final Image
# -------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# copy backend jar
COPY --from=backend-build /app/backend/target/*.jar ./app.jar

# copy frontend build in sb static
COPY --from=frontend-build /app/frontend/dist ./static

# port
ENV PORT=8080

ENTRYPOINT ["java","-jar","app.jar"]
