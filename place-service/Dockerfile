# Gradle
FROM gradle:8.13.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
<<<<<<< HEAD
ENTRYPOINT ["java", "-jar", "app.jar"]
=======
ENTRYPOINT ["java", "-jar", "app.jar"]

ARG TOUR_API_KEY
>>>>>>> 1230fbad88e7b4ae62a7080d04646e647a3a6793
