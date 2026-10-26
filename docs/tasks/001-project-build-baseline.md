# Task 001: Establish project package and build baseline

## Status
PARTIALLY_IMPLEMENTED

## Context
`pom.xml` already targets Java 21 and declares QuickFIX/J, JUnit, and Surefire, but production classes use the default package and no tests exist. Establish a reliable baseline before architectural changes.

## Goal
Create a clean, documented Java 21 build baseline without changing runtime behavior.

## Current State
Verified `pom.xml` contains Java 21, QuickFIX/J 2.3.2, JUnit Jupiter 5.10.1, and Surefire 3.2.5. No `src/test` files exist. Maven was unavailable during analysis.

## Requirements
- Keep Java 21 and current approved dependencies unless a separate decision authorizes change.
- Move or organize production code into named packages without changing behavior.
- Add a minimal build-smoke test and deterministic test defaults.
- Do not add Spring Boot or infrastructure dependencies.

## Files in Scope
`pom.xml`, `src/main/java/**`, `src/test/java/**`.

## Out of Scope
Routing behavior, session topology, production feature work, and dependency upgrades.

## Design Constraints
Java 21; QuickFIX/J; no blocking callback work; preserve existing user decisions.

## Error Handling
Build failure must fail CI clearly; test setup must fail fast on invalid Java/runtime configuration.

## Testing
Add a smoke test and run Maven with no arbitrary sleeps.

## Acceptance Criteria
- [ ] Sources compile under Java 21.
- [ ] At least one deterministic test runs.
- [ ] No production behavior is silently changed.
- [ ] Package/build decisions are documented.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
