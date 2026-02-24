# flower_shop
Spring Boot Flower Shop Project - это приложение для управления цветочным магазином, созданное на базе Spring Boot. Оно позволяет работникам вести учет цветов, настраивать их характеристики и работать с данными.Также поможет покупателям осуществлять покупки, самостоятельно собирать букеты.

Лабораторная  № 1 : Basic REST service
1. Создано Spring Boot приложение.
2. Реализовано REST API для одной ключевой сущности Flower.
3. Реализованы GET endpoint в FlowerController:
- GET запрос /{id} с использованием @PathVariable (метод getFlowerByCatalogNumber).
- GET запрос с использованием @RequestParam (метод getFlowersByColor).
4. Реализованы слои: FlowerController → FlowerService → FlowerRepository.
5. Реализовано DTO (FlowerDto) и маппер (FlowerMapper) между Entity (Flower) и API-ответом.

Ссылка Sonar : https://sonarcloud.io/api/project_badges/measure?project=MariaTukailo_flower_shop&metric=alert_status