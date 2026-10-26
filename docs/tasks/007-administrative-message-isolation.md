# Task 007: Implement administrative-message isolation

## Status
PARTIALLY_IMPLEMENTED

## Context
`fromAdmin` is empty, but the routing boundary is not explicitly enforced and `fromApp` accepts any message type.

## Goal
Guarantee that FIX administrative messages are processed only by their source QuickFIX/J session.

## Current State
`FixMultiplexer` looks up tag 35 without an admin allowlist.

## Requirements
- Classify admin versus application messages at the boundary.
- Never route `35=A, 5, 0, 1, 2, 3, 4` as business messages.
- Preserve QuickFIX/J session-generated admin behavior.

## FIX Behavior
Admin messages remain in-session in both directions; only `fromApp` application messages may enter routing.

## Files in Scope
Application callbacks, routing boundary, tests.

## Out of Scope
Custom admin message policy and business route definitions.

## Design Constraints
No mutation; independent session sequence numbers.

## Error Handling
Unexpected admin input is ignored or delegated to QuickFIX/J with an observable diagnostic; it must not crash callbacks.

## Testing
Unit-test every standard admin MsgType and integration-test logon/heartbeat/logout isolation.

## Acceptance Criteria
- [ ] No admin message reaches a different session.
- [ ] Application messages remain eligible.
- [ ] Admin isolation tests pass.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
