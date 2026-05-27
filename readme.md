# ExploreWithMe

ExploreWithMe - дипломный проект Practicum: сервис для публикации событий, подборок событий, заявок на участие, категорий, локаций, пользователей и статистики просмотров.

## Этап 2: микросервисы

На этапе 2 монолитная бизнес-логика вынесена из `main-server` в отдельные Spring Boot сервисы. Внешний REST API сохранён, а входной трафик маршрутизируется через `gateway-server`.

Сделано:

- `user-service` выделен для пользователей.
- `additional-service` выделен для категорий и локаций.
- `event-service` выделен для событий и подборок.
- `request-service` выделен для заявок на участие.
- `main-server` больше не содержит вынесенную бизнес-реализацию и остаётся служебным Spring Boot приложением.
- Gateway routes настроены на standalone-сервисы.
- Межсервисные вызовы выполняются через OpenFeign, service discovery идёт через Eureka.
- Конфигурации сервисов отдаются Config Server.

## Maven-модули

Основные модули проекта:

| Module | Назначение |
| --- | --- |
| `infra/config-server` | Spring Cloud Config Server |
| `infra/discovery-server` | Eureka Discovery Server |
| `infra/gateway-server` | Spring Cloud Gateway |
| `core/ewm-main-service/main-server` | служебный main-server |
| `core/user-service` | пользователи |
| `core/additional-service` | категории и локации |
| `core/event-service` | события и подборки |
| `core/request-service` | заявки на участие |
| `core/ewm-common` | общие DTO и lookup-интерфейсы |
| `ewm-stats-service/stats-server` | сервер статистики |
| `ewm-stats-service/stats-client` | клиент статистики |
| `ewm-stats-service/stats-dto` | DTO статистики |

## Архитектура сервисов

| Service | Ответственность |
| --- | --- |
| `gateway-server` | единая внешняя точка входа, маршрутизация публичного API |
| `discovery-server` | Eureka registry |
| `config-server` | централизованные YAML-конфиги |
| `main-server` | служебное приложение без вынесенной бизнес-логики |
| `user-service` | пользователи, internal user lookup |
| `additional-service` | категории, локации, internal category lookup |
| `event-service` | события, подборки, internal event lookup |
| `request-service` | заявки на участие, internal request lookup |
| `stats-server` | хранение и выдача статистики просмотров |

## Gateway routes

| External route | Destination |
| --- | --- |
| `/admin/users`, `/admin/users/**` | `lb://user-service` |
| `/categories`, `/categories/**` | `lb://additional-service` |
| `/admin/categories`, `/admin/categories/**` | `lb://additional-service` |
| `/locations`, `/locations/**` | `lb://additional-service` |
| `/admin/locations`, `/admin/locations/**` | `lb://additional-service` |
| `/events`, `/events/**` | `lb://event-service` |
| `/admin/events`, `/admin/events/**` | `lb://event-service` |
| `/users/*/events`, `/users/*/events/**` | `lb://event-service` |
| `/users/*/requests`, `/users/*/requests/**` | `lb://request-service` |
| `/users/*/events/*/requests`, `/users/*/events/*/requests/**` | `lb://request-service` |
| `/compilations`, `/compilations/**` | `lb://event-service` |
| `/admin/compilations`, `/admin/compilations/**` | `lb://event-service` |

Route ordering matters:

- request routes are placed above private event routes;
- admin routes for users/categories/locations/events/compilations are placed above the fallback `/admin/**`;
- internal routes are not published through Gateway.

## Internal API

Internal endpoints are intended only for service-to-service calls through Feign clients.

| Endpoint | Owner service | Consumer service | Назначение |
| --- | --- | --- | --- |
| `GET /internal/users/{userId}/exists` | `user-service` | `event-service`, `request-service` | проверка существования пользователя |
| `GET /internal/users/{userId}/short` | `user-service` | `event-service`, `request-service` | краткая информация о пользователе |
| `POST /internal/users/short` | `user-service` | `event-service`, `request-service` | краткая информация по списку пользователей |
| `GET /internal/categories/{categoryId}/exists` | `additional-service` | `event-service` | проверка существования категории |
| `GET /internal/categories/{categoryId}` | `additional-service` | `event-service` | категория по id |
| `POST /internal/categories` | `additional-service` | `event-service` | категории по списку id |
| `GET /internal/events/{eventId}/participation-info` | `event-service` | `request-service` | данные события для правил заявок |
| `GET /internal/requests/events/{eventId}/confirmed-count` | `request-service` | `event-service` | число подтверждённых заявок события |
| `POST /internal/requests/events/confirmed-counts` | `request-service` | `event-service` | число подтверждённых заявок по списку событий |

Через Gateway эти paths должны возвращать `404`.

## Межсервисные связи

Связи реализованы через интерфейсы из `ewm-common` и Feign clients:

- `event-service -> user-service`: `UserLookupService`;
- `event-service -> additional-service`: `CategoryLookupService`;
- `event-service -> request-service`: `RequestLookupService`;
- `request-service -> user-service`: `UserLookupService`;
- `request-service -> event-service`: `EventLookupService`.

Подборки (`compilations`) находятся внутри `event-service` и используют event/request lookup-данные для заполнения DTO событий.

Maven-зависимости между бизнес-сервисами не используются: сервисы не подтягивают реализации друг друга через classpath.

## Конфигурации

Локальные `application.yml` задают `spring.application.name`, порт и подключение к Config Server/Eureka. Централизованные конфиги лежат здесь:

```text
infra/config-server/src/main/resources/config/
```

Основные файлы:

- `user-service.yml`;
- `additional-service.yml`;
- `event-service.yml`;
- `request-service.yml`;
- `main-server.yml`;
- `stats-server.yml`.

Lookup modes для standalone-сервисов:

```yaml
ewm:
  event-service:
    users:
      lookup:
        mode: feign
    categories:
      lookup:
        mode: feign
  requests:
    lookup:
      mode: feign
```

```yaml
ewm:
  request-service:
    users:
      lookup:
        mode: feign
  events:
    lookup:
      mode: feign
```

## Docker

Запуск полного стека:

```bash
docker compose build
docker compose up -d
```

Основные адреса:

- Gateway: `http://localhost:8080`
- Eureka dashboard: `http://localhost:8761`

В `docker-compose.yml` есть:

- `discovery-server`;
- `config-server`;
- `gateway-server`;
- `main-db`;
- `stats-db`;
- `stats-server`;
- `main-server`;
- `user-service`;
- `additional-service`;
- `event-service`;
- `request-service`.

Бизнес-сервисы этапа 2 используют общую БД `ewm` через `main-db`. Отдельные `user-db`, `event-db`, `request-db`, `additional-db` не создаются.

## Проверка

Основная сборка:

```bash
mvn clean package
```

Минимальный smoke через Gateway:

1. `POST /admin/users`
2. `POST /admin/categories`
3. `POST /users/{userId}/events`
4. `PATCH /admin/events/{eventId}`
5. `POST /users/{secondUserId}/requests?eventId={eventId}`
6. `GET /users/{secondUserId}/requests`
7. `POST /admin/compilations`
8. `GET /compilations`
9. `GET /events`

Internal endpoints через Gateway должны оставаться закрытыми:

- `GET /internal/events/{eventId}/participation-info -> 404`
- `GET /internal/requests/events/{eventId}/confirmed-count -> 404`
- `GET /internal/users/{userId}/short -> 404`
- `GET /internal/categories/{categoryId} -> 404`

## Архитектурное допущение

На этапе 2 бизнес-сервисы используют общую БД `ewm` через `main-db`. Это осознанное промежуточное решение для этапа декомпозиции: сервисы уже разделены по процессам, маршрутам, Maven-зависимостям и межсервисным вызовам, но физическое разделение БД и окончательное удаление междоменных FK может быть выполнено отдельным следующим этапом.
