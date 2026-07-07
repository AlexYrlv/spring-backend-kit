# Architecture Decision Records

Decisions are recorded as [MADR](https://adr.github.io/madr/)-style documents:
one decision per file, numbered, never rewritten — superseded decisions get a
new record that links back.

| # | Decision | Status |
|---|----------|--------|
| [0001](0001-starters-over-shared-jar.md) | Ship shared functionality as Spring Boot starters aligned by a BOM | accepted |
| [0002](0002-layer-rules-enforced-by-archunit.md) | Enforce the layered architecture with ArchUnit tests | accepted |
| [0003](0003-virtual-threads-over-webflux.md) | Blocking programming model on Java 21 virtual threads | accepted |
