# Task 012: Add unavailable-session and backpressure policy

## Status
NOT_STARTED

## Context
Current sends can fail silently, and no queue or overload policy exists. A disconnected destination must not block or crash a QuickFIX/J callback.

## Goal
Define bounded, observable delivery behavior for unavailable or overloaded destinations.

## Current State
`FixMultiplexer` calls `Session.sendToTarget` directly and catches/ignores exceptions in one path.

## Requirements
- Use bounded queues only if asynchronous routing is selected.
- Preserve order per destination with one drain/serialized sender.
- Define reject/drop/expire policy and metrics for unavailable and full destinations.
- Bound all waits and queue operations.

## FIX Behavior
No message is sent to a logged-out session; session admin traffic remains QuickFIX/J-owned.

## Files in Scope
Delivery components, configuration, tests, operational docs.

## Out of Scope
Durable queues, Kafka, and clustering.

## Design Constraints
No blocking callback threads; bounded queues; per-destination order.

## Error Handling
Unavailable/full destinations produce explicit failure and diagnostics; they never cause process-wide failure.

## Testing
Test full queues, disconnects, ordering, and bounded completion.

## Acceptance Criteria
- [ ] Callback work is bounded.
- [ ] Queue capacity and failure policy are configured/documented.
- [ ] Ordering and overload tests pass.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
