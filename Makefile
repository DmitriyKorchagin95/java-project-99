setup:
	./gradlew clean build

app:
	./gradlew bootRun --args='--spring.profiles.active=production'

lint:
	./gradlew spotlessApply

test:
	./gradlew test

.PHONY: setup app clean build lint test