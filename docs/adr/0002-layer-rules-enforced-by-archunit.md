# Enforce the layered architecture with ArchUnit tests

- Status: accepted
- Date: 2026-07-07

## Context and Problem Statement

Every service built on the kit follows the same layered shape:
api → service → outbound/persistence, dependencies pointing one way only.
How do we keep this true across many services and contributors over time?

## Decision Drivers

- Rules that live only in documentation decay silently.
- Violations must be caught before merge, not in review comments.
- The cost of compliance for service authors must be near zero.

## Considered Options

1. Document the rules in READMEs and rely on code review.
2. Ship a shared ArchUnit rule set in `test-kit`; every service runs it in CI.
3. Split each layer into a separate Gradle module per service so the compiler
   enforces direction.

## Decision Outcome

Chosen option: **shared ArchUnit rule set** (option 2).

`test-kit` provides the rules — controllers may not touch repositories or
outbound clients, no cycles between packages, no field injection, outbound
`catch` blocks must record a metric or rethrow — and a service enables them
with one test class extending the base.

### Consequences

- Good: architecture violations fail the build with a named rule and the
  offending class; reviews stop policing structure.
- Good: the rule set itself is versioned — tightening a rule rolls out through
  the BOM like any other change.
- Bad: ArchUnit adds seconds to the test suite and occasional false positives;
  rules can be locally frozen (`FreezingArchRule`) to adopt them gradually.
- Rejected option 3 as too heavy for small services: one Gradle module per
  layer multiplies build files without adding safety beyond what the tests
  give.
