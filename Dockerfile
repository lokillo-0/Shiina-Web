FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Fix Alpine DNS + JVM stalls
ENV MAVEN_OPTS="-Djava.net.preferIPv4Stack=true -Xmx512m"

# Cache dependencies
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Build
COPY src ./src
RUN mvn -B clean package -DskipTests

# -----------------------------

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the JAR
COPY --from=build /app/target/*.jar /app/shiina.jar

# App resources
COPY static /app/static
COPY templates /app/templates

# Runtime dirs
RUN mkdir -p /app/logs /app/plugins /app/data /app/.cache

ENTRYPOINT ["java", "-jar", "/app/shiina.jar"]
