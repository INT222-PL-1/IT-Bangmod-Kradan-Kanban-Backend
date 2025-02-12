FROM maven:3.9.6-eclipse-temurin-17-alpine AS build-state
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-alpine
RUN mkdir /upload-dir
WORKDIR /app
COPY --from=build-state /app/target/*.jar ./app.jar
VOLUME /upload-dir
EXPOSE 8080
CMD ["java","-jar","app.jar","--spring.profiles.active=prod"]
