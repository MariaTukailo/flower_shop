# Flower shop
Spring Boot Flower Shop Project — это веб-приложение для управления цветочным магазином, разработанное с использованием Spring Boot.

Проект предназначен для автоматизации работы магазина и включает функциональность как для сотрудников, так и для покупателей.

Возможности для сотрудников:

 Добавление, редактирование и удаление цветов.Управление каталогом товаров.
  Настройка характеристик (цена, описание, наличие и др.).
   Поиск цветов по параметрам.

Возможности для покупателей:

Просмотр каталога цветов. Самостоятельная сборка букета. Добавление товаров в корзину. Оформление заказа.

Лабораторная  № 1 : Basic REST service
1. Создано Spring Boot приложение.
2. Реализовано REST API для одной ключевой сущности Flower.
3. Реализованы GET endpoint в FlowerController:
- GET запрос /{id} с использованием @PathVariable (метод getFlowerByCatalogNumber).
- GET запрос с использованием @RequestParam (метод getFlowersByColor).
4. Реализованы слои: FlowerController → FlowerService → FlowerRepository.
5. Реализовано DTO (FlowerDto) и маппер (FlowerMapper) между Entity (Flower) и API-ответом.

Ссылка Sonar : https://sonarcloud.io/api/project_badges/measure?project=MariaTukailo_flower_shop&metric=alert_status