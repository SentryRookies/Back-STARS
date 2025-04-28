# Gradle
FROM gradle:8.13.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

ARG TOUR_API_KEY

COPY .env .env