FROM maven:3.9.6-eclipse-temurin-17-alpine AS build-state
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build-state /app/target/*.jar ./app.jar
EXPOSE 8080
CMD ["java","-jar","app.jar"]
