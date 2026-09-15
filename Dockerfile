FROM eclipse-temurin:21-jdk-noble AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

RUN chmod +x gradlew
RUN ./gradlew bootJar -x test

FROM eclipse-temurin:21-jre-noble

WORKDIR /app

RUN groupadd --system springgroup \
    && useradd --system -g springgroup springuser

COPY --from=builder /app/build/libs/*.jar app.jar

RUN chown springuser:springgroup app.jar
USER springuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]