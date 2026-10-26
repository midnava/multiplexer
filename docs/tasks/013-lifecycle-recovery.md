# Task 013: Add graceful shutdown and recovery limitations

## Status
NOT_STARTED

## Context
`MultiplexerRunner` starts threads, loops with `Thread.sleep`, and waits for Enter; it has no coordinated shutdown or restart policy.

## Goal
Provide deterministic startup, graceful shutdown, and explicit correlation recovery behavior.

## Current State
Startup is hard-coded and readiness requires two clients; no stop handling or correlation restoration exists.

## Requirements
- Start components in dependency order and fail startup on invalid configuration.
- Stop accepting new work, drain/expire bounded deliveries, stop QuickFIX/J components, and close resources.
- Handle signals/close requests idempotently.
- Document that in-memory correlation is lost on restart unless explicitly persisted.

## FIX Behavior
Shutdown sends/handles session logout through QuickFIX/J; no admin messages are routed as business messages.

## Files in Scope
Runner/lifecycle classes, docs, tests.

## Out of Scope
Database persistence and clustering.

## Design Constraints
Graceful lifecycle; bounded waits; independent session stores.

## Error Handling
Startup failures clean up already-started components; shutdown timeout is reported and does not hang indefinitely.

## Testing
Test startup failure cleanup, idempotent stop, and bounded shutdown.

## Acceptance Criteria
- [ ] Startup/shutdown order is deterministic.
- [ ] No new routing is accepted after shutdown begins.
- [ ] Restart correlation limitations are documented and tested where applicable.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
