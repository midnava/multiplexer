# Task 015: Build isolated FIX client/server integration harness

## Status
NOT_STARTED

## Context
There are no test sources. The emulator classes and cfg files provide a starting point but no repeatable automated client/server verification.

## Goal
Create end-to-end tests proving session isolation, routing, correlation, and lifecycle behavior.

## Current State
`MarketEmulatorApp` prints received messages; `MultiplexerRunner` manually sleeps and waits for console input.

## Requirements
- Start isolated in-process QuickFIX/J test server and clients on dynamically allocated ports.
- Use temporary per-session stores and clean them after tests.
- Verify client-to-upstream, upstream-to-owner, same-ID collision, admin isolation, disconnect, and reconnect behavior.
- Use bounded `CompletableFuture`, `CountDownLatch`, or `BlockingQueue` waits; no arbitrary sleeps.

## FIX Behavior
Exercise `35=A`, `0`, `5`, and application messages with independent sequence numbers; assert admin messages are not forwarded.

## Files in Scope
`src/test/java/**`, test resources, test utilities.

## Out of Scope
Production feature implementation, performance/load testing, and external deployment.

## Design Constraints
QuickFIX/J; isolated stores; deterministic bounded tests.

## Error Handling
Test failures include captured messages/session state; setup and teardown failures clean up resources.

## Testing
This task is itself the integration test suite and must run under Maven Surefire.

## Acceptance Criteria
- [ ] Test client and upstream server connect through the multiplexer.
- [ ] Responses reach only the correct client.
- [ ] Admin isolation, collision, disconnect, and reconnect cases pass.
- [ ] No shared persistent stores or arbitrary sleeps exist.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
