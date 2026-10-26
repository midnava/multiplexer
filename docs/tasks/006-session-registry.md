# Task 006: Implement thread-safe session registry

## Status
PARTIALLY_IMPLEMENTED

## Context
`MultiplexerApp` uses a list and hard-coded session identity, while routing needs explicit client/upstream ownership.

## Goal
Provide a thread-safe registry mapping logical roles to live QuickFIX/J sessions.

## Current State
Client sessions are stored in `CopyOnWriteArrayList`; the market is one volatile `SessionID`.

## Requirements
- Register/unregister sessions atomically by logical name and exact `SessionID`.
- Track logged-on/available state and prevent duplicate ownership.
- Never derive destination identity from inbound headers.

## FIX Behavior
The registry owns session selection only; QuickFIX/J owns `34`, `49`, `56`, and admin traffic.

## Files in Scope
Registry and lifecycle classes, tests.

## Out of Scope
Route algorithms and correlation persistence.

## Design Constraints
Thread-safe state; immutable configuration; independent sessions.

## Error Handling
Duplicate registration is rejected or idempotent only for the same session; unknown/unavailable lookup returns a typed failure.

## Testing
Concurrent register/unregister and exact session lookup tests.

## Acceptance Criteria
- [ ] Logical destination lookup is deterministic.
- [ ] Duplicate identities cannot overwrite another session.
- [ ] Logout removes routing eligibility.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
