# Stage 1: Build the application
FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY --chown=gradle:gradle . .
# Skip tests for faster deployment
RUN gradle clean build -x test --no-daemon

# Stage 2: Production environment
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
EXPOSE 8080

# Copy the jar file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Set environment variables with defaults (will be overwritten by Render)
ENV SPRING_PROFILES_ACTIVE=prod
ENV DB_URL=jdbc:postgresql://localhost:5432/jobhunter_db
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=123456

ENTRYPOINT ["java", "-jar", "app.jar"]
