setup:
	./gradlew clean build

build:
	./gradlew build

start:
	./gradlew bootRun --args='--spring.profiles.active=development'

lint:
	./gradlew spotlessApply

test:
	./gradlew test

clean:
	./gradlew clean

.PHONY: setup build start app lint test clean