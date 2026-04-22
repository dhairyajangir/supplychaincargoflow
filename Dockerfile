FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

COPY server/pom.xml ./pom.xml
COPY server/src ./src

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]