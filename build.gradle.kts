plugins {
    application
    jacoco
    checkstyle
    alias(libs.plugins.sentryJvmGradle)
    alias(libs.plugins.benManes)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.lombok)
    alias(libs.plugins.springBoot)
    alias(libs.plugins.dependencyManagement)
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

application {
    mainClass.set("hexlet.code.AppApplication")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.oauth2.resource.server)
    implementation(libs.jackson.databind.nullable)
    implementation(libs.mapstruct)
    implementation(libs.datafaker)
    implementation(libs.instancio.junit)
    implementation(libs.springdoc.openapi)
    developmentOnly(libs.spring.boot.devtools)
    annotationProcessor(libs.spring.boot.configuration.processor)
    annotationProcessor(libs.mapstruct.processor)
    runtimeOnly(libs.h2)
    runtimeOnly(libs.postgresql)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.json.unit.assertj)
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml"))
    }
}

tasks.bootJar {
    archiveFileName.set("app.jar")
}

sonar {
    properties {
        property("sonar.projectKey", "DmitriyKorchagin95_java-project-99")
        property("sonar.organization", "dmitriykorchagin95")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

sentry {
    includeSourceContext = true
    org = "null-06n"
    projectName = "java-project-99"
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
}
