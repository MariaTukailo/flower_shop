# --- ЭТАП 1: Сборка Frontend (React + Vite) ---
# Меняем на 20, чтобы Vite не ругался на CustomEvent
FROM node:20 AS frontend-build
WORKDIR /app/frontend

# Копируем конфиги фронта
COPY flower_shop/package*.json ./
RUN npm install

# Копируем весь исходный код фронта и собираем
COPY flower_shop/ ./
RUN npm run build

# --- ЭТАП 2: Сборка Backend (Spring Boot) ---
FROM maven:3.8.5-eclipse-temurin-17 AS backend-build
WORKDIR /app

# Копируем pom и качаем зависимости (бэкенд)
COPY pom.xml .
RUN mvn dependency:go-offline

# Копируем исходники бэкенда
COPY src ./src

# КРИТИЧЕСКОЕ ИСПРАВЛЕНИЕ ПУТИ:
# Мы собирали фронт в /app/frontend, значит результат лежит в /app/frontend/dist
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static

RUN mvn clean package -DskipTests

# --- ЭТАП 3: Финальный образ ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]