# ADR-0001: Ship shared functionality as Spring Boot starters + BOM, not a shared jar

Status: accepted · Date: 2026-07-07

## Context

Multiple backend services need the same cross-cutting functionality: error
model, observability, Redis utilities, resilience defaults, test bases. The
two common delivery mechanisms are a single shared `common.jar` and a set of
per-capability Spring Boot starters aligned by a BOM.

A shared jar grows into a dependency magnet: every service pulls every
transitive dependency whether it uses the capability or not, upgrades become
all-or-nothing, and unused auto-wired beans leak into application contexts.

## Decision

- Each capability ships as its own thin `*-spring-boot-starter` with
  `@AutoConfiguration` and `@ConditionalOn...` guards, defaults overridable
  via `application.yml`.
- A single `kit-bom` (Gradle platform) aligns versions; services import the
  BOM and declare starters without versions.
- A reference `sample-service` consumes every starter and acts as the upgrade
  canary: if it goes red, a starter broke composition.

## Consequences

- Services pay only for what they import; contexts stay clean.
- Per-capability semantic versioning; a breaking change in one starter does
  not force a fleet-wide upgrade.
- Cost: more modules to maintain and publish; mitigated by a shared Gradle
  convention plugin and one CI pipeline for the whole repo.
