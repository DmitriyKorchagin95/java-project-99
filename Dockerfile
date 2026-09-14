FROM eclipse-temurin:21-jdk-noble AS builder

RUN apt-get update && apt-get install -yq --no-install-recommends make unzip && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY gradle gradle
COPY gradle.properties .
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY gradlew .
RUN ./gradlew --no-daemon dependencies

COPY lombok.config .
COPY src src

RUN ./gradlew --no-daemon build

FROM eclipse-temurin:21-jre-noble
WORKDIR /app

RUN groupadd --system springgroup && useradd --system -g springgroup springuser

COPY --from=builder --chown=springuser:springgroup "/app/build/libs/app-0.0.1-SNAPSHOT.jar" app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=60.0 -XX:InitialRAMPercentage=50.0"
EXPOSE 8080

USER springuser

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
