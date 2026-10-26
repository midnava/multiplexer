# Task 003: Separate QuickFIX/J session settings and stores

## Status
PARTIALLY_IMPLEMENTED

## Context
`multiplexer.cfg` contains one Initiator and two Acceptors, while `clients.cfg` contains client Initiators. The runner also starts a market emulator. Required downstream and upstream roles must be explicit and independent.

## Goal
Define production session settings with unique identities and isolated stores.

## Current State
Both downstream sessions use port 9880 and `MemoryStoreFactory` is passed to all components. The emulator configuration is separate.

## Requirements
- Provide separate downstream Acceptor and upstream Initiator settings.
- Use unique `SessionID`s and per-session stores.
- Keep emulator settings test-only/demo-only.
- Ensure sequence numbers are owned by each QuickFIX/J session.

## FIX Behavior
Each connection has independent `34` sequence state. Never forward source `34`, `49`, `56`, or `52` as destination session identity/data.

## Files in Scope
`src/main/resources/*.cfg`, session startup classes, tests.

## Out of Scope
Business routing and correlation.

## Design Constraints
QuickFIX/J; independent FIX sessions; isolated temporary stores in tests.

## Error Handling
Duplicate or unusable settings fail startup; store initialization failures are fatal.

## Testing
Parse settings and verify unique IDs, roles, ports, and store paths.

## Acceptance Criteria
- [ ] Downstream and upstream roles are unambiguous.
- [ ] No production session shares a sequence store.
- [ ] Demo emulator is not required for production startup.
- [ ] Settings tests pass.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
