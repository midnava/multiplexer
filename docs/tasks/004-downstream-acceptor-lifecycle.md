# Task 004: Implement downstream Acceptor lifecycle

## Status
PARTIALLY_IMPLEMENTED

## Context
`MultiplexerRunner` starts a `ThreadedSocketInitiator` for `clients.cfg`, contrary to the requirement that client connections be accepted by the multiplexer.

## Goal
Own startup, logon, logout, and shutdown of the downstream QuickFIX/J Acceptor.

## Current State
`MultiplexerApp` keeps a `CopyOnWriteArrayList` of clients and removes them on logout, but lifecycle ownership is absent and client sessions are configured as Initiators.

## Requirements
- Start one managed Acceptor from validated downstream settings.
- Register sessions on creation/logon and remove them on logout/disconnect.
- Expose lifecycle state without using callback-thread blocking.
- Reject sends to sessions that are not logged on.

## FIX Behavior
Acceptor handles client `35=A`, `5`, heartbeats, logouts, and sequence numbers through QuickFIX/J; these are not business-routed.

## Files in Scope
Session lifecycle/application classes, downstream settings, tests.

## Out of Scope
Upstream Initiator implementation and business routing.

## Design Constraints
Independent sessions; no callback blocking; QuickFIX/J owns admin traffic.

## Error Handling
Startup failure is fatal; logout removes routing eligibility; unavailable client sends return a bounded failure.

## Testing
Use a test FIX Initiator client and bounded latches/queues; no arbitrary sleeps.

## Acceptance Criteria
- [ ] Clients connect to the multiplexer Acceptor.
- [ ] Session registry reflects logon/logout.
- [ ] Acceptor stops cleanly.
- [ ] Lifecycle integration test passes.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
