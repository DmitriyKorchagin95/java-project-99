FROM gradle:9.1.0-jdk21 AS builder

WORKDIR /app

COPY . .

RUN ./gradlew clean build --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=production"]