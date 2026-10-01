plugins {
	application
	jacoco
	checkstyle
	id("com.github.ben-manes.versions") version "0.52.0"
	id("com.diffplug.spotless") version "7.0.2"
	id("org.sonarqube") version "5.0.0.4638"
	id("io.freefair.lombok") version "8.12.2"
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

application {
	mainClass = "hexlet.code.AppApplication"
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	implementation("org.openapitools:jackson-databind-nullable:_")
	implementation("org.mapstruct:mapstruct:_")
	annotationProcessor("org.mapstruct:mapstruct-processor:_")
	implementation("net.datafaker:datafaker:_")
	implementation("org.instancio:instancio-junit:_")
	implementation("io.sentry:sentry-spring-boot-starter-jakarta:_")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:_")
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

spotless {
	java {
		importOrder()
		removeUnusedImports()
		formatAnnotations()
		leadingTabsToSpaces(4)
		endWithNewline()
		trimTrailingWhitespace()
	}
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml"))
	}
}

sonar {
	properties {
		property("sonar.projectKey", "DmitriyKorchagin95_java-project-99")
		property("sonar.organization", "dmitriykorchagin95")
		property("sonar.host.url", "https://sonarcloud.io")
		property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
	}
}
