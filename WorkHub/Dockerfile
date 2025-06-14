# Stage 1: Build the app
FROM eclipse-temurin:17-jdk-alpine as builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first (cache dependencies)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline

# Copy the rest of the source
COPY src src

# Build the app
RUN ./mvnw package -DskipTests

# Stage 2: Run the app
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built jar from stage 1
COPY --from=builder /app/target/*.jar app.jar

# Railway hoặc Docker sẽ truyền biến PORT
ENV PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]
