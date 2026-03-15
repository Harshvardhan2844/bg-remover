# Stage 1: Build the project
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy only the backend folder contents (where pom.xml lives)
COPY backend/ .

# Build the project and skip tests to save time
RUN mvn clean package -DskipTests

# Stage 2: Run the project
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]