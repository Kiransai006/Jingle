# ---------- build stage ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package -DskipTests

# ---------- runtime stage ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S jingle && adduser -S jingle -G jingle
COPY --from=build /app/target/jingle-backend.jar app.jar
USER jingle
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
