setup:
	./gradlew clean build

build:
	./gradlew build

app:
	./gradlew bootRun --args='--spring.profiles.active=production'

lint:
	./gradlew spotlessApply

test:
	./gradlew test

clean:
	./gradlew clean

.PHONY: setup build app lint test clean