# Task 005: Implement upstream Initiator lifecycle

## Status
PARTIALLY_IMPLEMENTED

## Context
The prototype starts an Initiator, but only one hard-coded market session is recognized and reconnect behavior is not managed.

## Goal
Own one or more configured upstream Initiator sessions with reconnect and lifecycle reporting.

## Current State
`MultiplexerApp` compares `TargetCompID` to `EXCHANGE_ID`; `marketSessionID` is a single volatile value and no explicit reconnect policy exists.

## Requirements
- Start Initiators from validated upstream definitions.
- Register each logical upstream on logon and remove it on logout.
- Support QuickFIX/J reconnect settings without busy loops.
- Keep upstream lifecycle separate from downstream lifecycle.

## FIX Behavior
Initiator owns upstream logon/admin messages and independent sequence numbers. No upstream admin message enters application routing.

## Files in Scope
Upstream lifecycle/application classes, settings, tests.

## Out of Scope
Response correlation and business route selection.

## Design Constraints
QuickFIX/J; independent sessions; non-blocking callbacks.

## Error Handling
Connection refusal is observable and retryable; startup configuration errors are fatal; logout makes the destination unavailable.

## Testing
Test connect, disconnect, and reconnect with bounded timeouts and isolated stores.

## Acceptance Criteria
- [ ] Multiple configured upstreams are supported.
- [ ] Reconnect does not duplicate registry entries.
- [ ] Lifecycle state is observable.
- [ ] Tests pass without `Thread.sleep`.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
