FROM eclipse-temurin:21-jdk-noble AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY config config
COPY src src

RUN chmod +x gradlew && ./gradlew bootJar -x test

FROM eclipse-temurin:21-jre-noble

WORKDIR /app

RUN groupadd --system springgroup && useradd --system -g springgroup springuser

COPY --from=builder --chown=springuser:springgroup /app/build/libs/*.jar app.jar

USER springuser

ENTRYPOINT ["java", "-jar", "app.jar"]
