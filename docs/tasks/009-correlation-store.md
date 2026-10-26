# Task 009: Implement collision-safe correlation store

## Status
PARTIALLY_IMPLEMENTED

## Context
`ownershipMap` keys only by extracted ID, so two clients using the same `ClOrdID`/`QuoteReqID` collide globally and entries never expire.

## Goal
Correlate requests to owning downstream sessions with explicit collision and lifecycle rules.

## Current State
`ConcurrentHashMap<String, SessionID>` uses a bare extractor result and `putIfAbsent`.

## Requirements
- Define a typed correlation key including source session and applicable MsgType/business ID.
- Atomically create ownership and reject conflicting ownership.
- Define expiry, completion, logout cleanup, and duplicate-response behavior.
- Document restart loss unless durable recovery is later approved.

## FIX Behavior
Correlation identifiers are business fields such as `11` or `131`; FIX session sequence fields are never correlation keys.

## Files in Scope
Correlation classes, routing configuration, tests, `docs/ROUTING.md`.

## Out of Scope
Database persistence and clustering.

## Design Constraints
Thread-safe state; bounded timeouts; independent sessions.

## Error Handling
Conflicts are rejected to the later client; missing/expired keys produce a non-broadcast failure; duplicate responses are not delivered twice.

## Testing
Test same IDs from different clients, expiry, logout, and concurrent insertion.

## Acceptance Criteria
- [ ] Same bare ID from two clients cannot misroute responses.
- [ ] Ownership operations are atomic.
- [ ] Expiry and cleanup are tested and documented.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
