# Blocking programming model on Java 21 virtual threads

- Status: accepted
- Date: 2026-07-07

## Context and Problem Statement

Services are IO-bound: they call databases, Redis and downstream HTTP APIs.
Which concurrency model should the kit standardize on — reactive (WebFlux) or
blocking code on virtual threads (Project Loom)?

## Decision Drivers

- Code must stay readable for authors coming from any background.
- Throughput must not be capped by platform-thread pools.
- Debuggability: stack traces and profilers must remain useful.

## Considered Options

1. Reactive stack (WebFlux + Reactor) everywhere.
2. Blocking Spring MVC on platform threads.
3. Blocking Spring MVC with virtual threads enabled
   (`spring.threads.virtual.enabled=true`).

## Decision Outcome

Chosen option: **Spring MVC on virtual threads** (option 3).

Plain imperative code — no `Mono`/`Flux` composition, no reactive-only
libraries — while each request runs on a cheap virtual thread, giving
async-grade concurrency for IO-bound work.

### Consequences

- Good: straight-line code, real stack traces, standard debugging and
  profiling; JDBC and every blocking client work unchanged.
- Good: one mental model across all kit modules, including workers.
- Bad: `synchronized` blocks that guard IO can pin virtual threads; the kit
  prefers `ReentrantLock` and documents the pinning pitfalls.
- Bad: true streaming endpoints still need reactive types; allowed locally
  (`Flux<ServerSentEvent>` for SSE) without adopting the full reactive stack.
- Rejected option 1: reactive pays a permanent complexity tax on every line
  for a throughput benefit virtual threads now deliver for IO-bound loads.
