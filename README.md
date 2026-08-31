### Hexlet tests and linter status:
[![Actions Status](https://github.com/DmitriyKorchagin95/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/DmitriyKorchagin95/java-project-99/actions)
[![build](https://github.com/DmitriyKorchagin95/java-project-99/actions/workflows/build.yml/badge.svg)](https://github.com/DmitriyKorchagin95/java-project-99/actions/workflows/build.yml)
___
### Sonarqube status:
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=DmitriyKorchagin95_java-project-99&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=DmitriyKorchagin95_java-project-99)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=DmitriyKorchagin95_java-project-99&metric=bugs)](https://sonarcloud.io/summary/new_code?id=DmitriyKorchagin95_java-project-99)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=DmitriyKorchagin95_java-project-99&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=DmitriyKorchagin95_java-project-99)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=DmitriyKorchagin95_java-project-99&metric=coverage)](https://sonarcloud.io/summary/new_code?id=DmitriyKorchagin95_java-project-99)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=DmitriyKorchagin95_java-project-99&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=DmitriyKorchagin95_java-project-99)
___
# Менеджер задач

Веб-приложение для управления задачами, разработанное на Java и Spring Boot.

Приложение позволяет создавать и редактировать задачи, назначать исполнителей, менять статусы и использовать метки для удобной группировки и фильтрации задач.

## Функционал

- регистрация и аутентификация пользователей;
- JWT-аутентификация и авторизация;
- CRUD пользователей;
- CRUD задач;
- CRUD статусов задач;
- CRUD меток;
- назначение исполнителя задачи;
- изменение статуса задачи;
- добавление нескольких меток к задаче;
- фильтрация задач по:
  - названию;
  - исполнителю;
  - статусу;
  - метке;
- пагинация списка задач;
- валидация входных данных;
- автоматическое заполнение даты создания;
- обработка ошибок;
- сбор ошибок через Sentry / Bugsink;
- интерактивная документация API через Swagger / OpenAPI;
- интеграционные тесты REST API.

## Технологии

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- PostgreSQL
- Gradle
- Docker
___

# Демо
### [Application on Render]()