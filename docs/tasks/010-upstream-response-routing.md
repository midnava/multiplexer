# Task 010: Implement upstream-to-client response routing

## Status
PARTIALLY_IMPLEMENTED

## Context
`routeFromMarket` can broadcast or owner-route, but ownership is incomplete and unknown responses are silently dropped.

## Goal
Deliver each upstream application response only to its correlated downstream owner.

## Current State
`FixMultiplexer` uses one ownership map and a configured `OWNER_ONLY` strategy; no response lifecycle or multi-upstream handling exists.

## Requirements
- Resolve responses through the correlation store.
- Route only to the owning live client.
- Support configured response MsgTypes and completion semantics.
- Never broadcast by default.

## FIX Behavior
Upstream application responses flow to the owning client; `35` and business correlation fields are preserved as application data, while destination session headers are regenerated.

## Files in Scope
Response router, correlation integration, tests.

## Out of Scope
New business message types not present in configuration.

## Design Constraints
Per-destination ordering; no callback blocking; safe copies.

## Error Handling
Unknown/expired correlation is logged/metriced and discarded or explicitly rejected; disconnected clients do not block upstream callbacks.

## Testing
Test two clients with identical IDs and verify exactly one receives the response; test unknown and late responses.

## Acceptance Criteria
- [ ] Responses are never accidentally broadcast.
- [ ] Correct client receives its response.
- [ ] Unknown and unavailable owners have deterministic outcomes.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
