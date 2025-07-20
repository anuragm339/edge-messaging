# Stage 1: Build
FROM eclipse-temurin:21-jdk as builder
WORKDIR /workspace
COPY . .
RUN ./gradlew :pos-messaging-platform:shadowJar

# Stage 2: Run
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /workspace/pos-messaging-platform/build/libs/pos-messaging-platform-all.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
