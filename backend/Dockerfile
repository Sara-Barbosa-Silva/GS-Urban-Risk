# Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

# Execução
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system urbanrisk && mkdir -p /app/data && chown urbanrisk /app/data
USER urbanrisk
COPY --from=build /app/target/urban-risk-backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
