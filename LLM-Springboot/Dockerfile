# ==================== Stage 1: Build ====================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the parent POM and module POMs first (for dependency caching)
COPY pom.xml .
COPY LLM-Data/pom.xml LLM-Data/pom.xml
COPY LLM-SB/pom.xml LLM-SB/pom.xml

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B || true

# Copy the full source code
COPY LLM-Data/src LLM-Data/src
COPY LLM-SB/src LLM-SB/src

# Build both modules, skip tests
RUN mvn clean package -DskipTests -B

# ==================== Stage 2: Run ====================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the executable JAR from the LLM-SB module
COPY --from=build /app/LLM-SB/target/LLM-SB-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

