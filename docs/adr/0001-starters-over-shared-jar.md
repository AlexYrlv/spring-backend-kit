# Ship shared functionality as Spring Boot starters aligned by a BOM

- Status: accepted
- Date: 2026-07-07

## Context and Problem Statement

Multiple backend services need the same cross-cutting functionality: error
model, observability, Redis utilities, resilience defaults, test bases. How
should this shared code be delivered so that services stay lean and upgrades
stay cheap?

## Decision Drivers

- A service must pay only for the capabilities it actually uses.
- Version upgrades must be a one-line change per service.
- Unused auto-wired beans must not leak into application contexts.

## Considered Options

1. A single shared `common.jar` all services depend on.
2. Per-capability Spring Boot starters aligned by a BOM.
3. Copy-paste templates (no shared artifacts at all).

## Decision Outcome

Chosen option: **per-capability starters + BOM**.

- Each capability is its own thin `*-spring-boot-starter` with
  `@AutoConfiguration` and `@ConditionalOn...` guards; defaults overridable
  via `application.yml`.
- A single `kit-bom` (Gradle platform) pins versions; services import the BOM
  and declare starters without versions.
- A reference `sample-service` consumes every starter and acts as the upgrade
  canary: if it goes red, a starter broke composition.

### Consequences

- Good: clean application contexts; per-capability semantic versioning; a
  breaking change in one starter does not force a fleet-wide upgrade.
- Good: this is the mechanism the Spring ecosystem itself uses (Spring Cloud),
  so every Spring developer already knows how to consume it.
- Bad: more modules to maintain and publish — mitigated by a shared Gradle
  convention plugin and one CI pipeline for the whole repo.
- Rejected option 1 because a shared jar becomes a dependency magnet: every
  service pulls every transitive dependency, and upgrades become
  all-or-nothing.
- Rejected option 3 because copy-paste forks drift immediately and fixes stop
  propagating.
