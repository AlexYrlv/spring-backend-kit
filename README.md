# spring-backend-kit

Paved-road foundation for Spring Boot backend services: a **BOM** + a set of thin
**Spring Boot starters** + a **reference service** that proves they compose.

Status: **design phase**. This README and `docs/adr/` are the plan; modules land
one by one (see Roadmap).

## Why

We build many backend services with the same shape:

```
REST API  →  service layer  →  outbound clients / storage
```

A mature set of building blocks for this already exists in our Python stack
(config conventions, REST client layer, Redis cache/locks/RPC, background
workers, test kit). This repo is the same foundation for the JVM — so starting
a new Java service means: generate from start.spring.io, apply the BOM, pick
the starters you need, write domain logic. Nothing else.

## How it is delivered

The idiomatic mechanism for shared platform code in the Spring ecosystem —
the same one Spring Cloud and large engineering orgs use:

- **`kit-bom`** — a Bill of Materials as the single version-alignment point.
  Services import one BOM and never pin transitive versions by hand.
- **Thin starters, not a god `common.jar`.** Each capability ships as its own
  `*-spring-boot-starter` with `@AutoConfiguration`, `@ConditionalOn...` beans
  and sensible defaults overridable via `application.yml`. A service pays only
  for what it imports. (See ADR-0001.)
- **`sample-service`** — an always-green reference service wired with every
  starter; doubles as living documentation and an upgrade canary.

## Architecture of a kit-based service

```
            ┌──────────────────────────────┐
            │           api layer          │  @RestController · DTO + Jakarta Validation
            │                              │  errors: RFC 9457 ProblemDetail
            └──────────────┬───────────────┘
                           │
            ┌──────────────▼───────────────┐
            │         service layer        │  @Service · business logic
            │                              │  @Transactional boundaries
            └────┬─────────────────────┬───┘
                 │                     │
      ┌──────────▼─────────┐ ┌─────────▼──────────┐
      │   outbound layer   │ │    persistence     │
      │ RestClient /       │ │ Spring Data        │
      │ @HttpExchange      │ │ JPA / MongoDB      │
      │ + Resilience4j     │ │ + Flyway           │
      └────────────────────┘ └────────────────────┘

  cross-cutting (starters): observability · resilience · caching · locks
```

- **One direction of dependencies:** api → service → outbound/persistence.
  Controllers never touch repositories or clients directly.
- **Layer rules are enforced, not documented:** a shared **ArchUnit** rule set
  runs in every service's test suite and fails the build on violations.
- **DTOs at the boundary, records inside.** Constructor injection only.
- **Blocking style on Java 21 virtual threads** — simple imperative code with
  async-grade throughput; reactive (WebFlux) only where streaming demands it.

## Modules

| Module | What it gives | Built on | Status |
|---|---|---|---|
| `kit-bom` | Version alignment for services | Gradle platform | design |
| `core-starter` | RFC 9457 `ProblemDetail` error model, request/correlation-id logging, config conventions, graceful shutdown defaults | Spring MVC, SLF4J/MDC | design |
| `observability-starter` | Metrics, health, domain counters (funnel/loss), Sentry wiring | Actuator, Micrometer, Prometheus | design |
| `resilience-starter` | Default timeouts, retries, circuit breakers for outbound HTTP | Resilience4j | design |
| `redis-starter` | Prefix-based key router, cache TTL configuration, distributed locks, scheduled-job locking | Spring Data Redis, Redisson, ShedLock | design |
| `redis-rpc-starter` | Request/reply RPC over Redis Pub/Sub | Spring Data Redis | design |
| `worker-starter` | Background workers: scheduled loops, queue consumers, graceful shutdown | Spring Scheduling, virtual threads | design |
| `test-kit` | Integration-test base classes, shared ArchUnit rules | Testcontainers (PostgreSQL/MongoDB/Redis), WireMock, ArchUnit | design |
| `sample-service` | Reference CRUD service wired with all of the above, OpenAPI docs | springdoc-openapi, Flyway | design |

## Deliberately NOT reimplementing (Spring covers it)

| Need | Spring answer |
|---|---|
| Config sections per component | `@ConfigurationProperties(prefix = "...")` + profiles |
| Method-level cache | `@Cacheable` + Redis backend |
| Repositories / query methods | Spring Data (JPA / MongoDB) |
| Outbound HTTP clients | `RestClient` / declarative `@HttpExchange` interfaces |
| Partial updates (PATCH) | `JsonNullable` + JPA dirty checking |
| Transactions | `@Transactional` |
| Server-sent events | `SseEmitter` / `Flux<ServerSentEvent>` |
| Object mapping | MapStruct |

## Engineering standards

- Java 21 · Spring Boot 3.x · Gradle multi-module with a version catalog
- Semantic versioning; artifacts published to GitHub Packages
- Architecture decisions recorded as ADRs in [`docs/adr/`](docs/adr/)
- API style baseline: [Zalando RESTful API Guidelines](https://opensource.zalando.com/restful-api-guidelines/)
- CI: build + tests on every push; `sample-service` must stay green

## Roadmap

- [ ] Gradle multi-module skeleton + version catalog + `kit-bom`
- [ ] `sample-service`: CRUD with PostgreSQL, Flyway, Testcontainers, springdoc
- [ ] `core-starter` (ProblemDetail + logging conventions)
- [ ] `test-kit` (Testcontainers bases + ArchUnit rules)
- [ ] `observability-starter`
- [ ] `redis-starter`
- [ ] `resilience-starter`
- [ ] `worker-starter`
- [ ] `redis-rpc-starter`
- [ ] CI + publishing to GitHub Packages

## References

- [Spring Boot — Creating Your Own Auto-configuration](https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html)
- [Zalando RESTful API Guidelines](https://opensource.zalando.com/restful-api-guidelines/)
- [ArchUnit](https://www.archunit.org/)
- [Testcontainers](https://testcontainers.com/)
- [Resilience4j](https://resilience4j.readme.io/)

## License

MIT
