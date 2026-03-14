# Stage 1: Build the project
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Set working directory
WORKDIR /app

# Copy all backend files
COPY . .

# Build the project and skip tests to save time
RUN mvn clean package -DskipTests

# Stage 2: Run the project
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Start the Spring Boot application
ENTRYPOINT ["java","-jar","app.jar"]