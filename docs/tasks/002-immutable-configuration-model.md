# Task 002: Define validated immutable configuration model

## Status
NOT_STARTED

## Context
Routing rules are hard-coded in `MultiplexerRunner`; QuickFIX settings are loaded directly in startup code. Later session and routing work needs one validated immutable model.

## Goal
Represent session roles and application routes as immutable typed configuration.

## Current State
`MessageRouteConfig` is a mutable-function record and `FixMultiplexer` stores routes in a mutable `HashMap`. No external routing configuration exists.

## Requirements
- Define downstream and upstream session definitions, route definitions, identifier extraction, and response policy.
- Load configuration before starting QuickFIX/J.
- Reject duplicate names/session IDs, unknown destinations, missing required fields, invalid ports, ambiguous routes, and unsupported strategies.
- Expose read-only validated values.

## FIX Behavior
Routes must identify application `35` values and required business identifiers; session headers and sequence fields are not routing configuration.

## Files in Scope
`src/main/java/**`, `src/main/resources/**`, `docs/CONFIGURATION.md`.

## Out of Scope
Opening sessions and sending messages.

## Design Constraints
Java 21; immutable validated configuration; no Spring Boot.

## Error Handling
Startup fails with actionable validation errors; no partially configured multiplexer is created.

## Testing
Unit-test valid configurations and every validation failure using temporary resources where needed.

## Acceptance Criteria
- [ ] Configuration is immutable after construction.
- [ ] Invalid/ambiguous definitions fail before QuickFIX/J startup.
- [ ] Routes reference existing logical destinations.
- [ ] Tests cover valid and invalid cases.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
