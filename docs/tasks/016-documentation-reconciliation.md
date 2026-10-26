# Task 016: Reconcile documentation and configuration examples

## Status
NOT_STARTED

## Context
The repository currently has no README/docs, configuration comments contain encoding corruption, and the demo topology differs from the required production topology.

## Goal
Make project documentation, examples, and operational assumptions match verified implementation.

## Current State
Only Java comments and three cfg files document behavior; no test or recovery guidance exists.

## Requirements
- Update `README.md` or project docs with supported topology, startup, testing, and limitations.
- Document every configuration field and route rule.
- Distinguish demo emulator settings from production settings.
- Document sequence ownership, admin isolation, correlation collision policy, unavailable-session behavior, and restart limitations.
- Remove claims unsupported by code/tests.

## FIX Behavior
Document application versus admin MsgTypes, required business identifiers, session ownership, destination-generated headers, and independent sequence numbers.

## Files in Scope
`README.md`, `docs/**`, `src/main/resources/**` examples.

## Out of Scope
Implementing production functionality, changing dependencies, and adding deployment infrastructure.

## Design Constraints
Documentation must reflect Java 21 and QuickFIX/J and must not promise unsupported recovery.

## Error Handling
Document startup validation failures, missing routes, unavailable sessions, and shutdown failures with their implemented outcomes.

## Testing
Cross-check examples against configuration/parser tests and run the full test suite.

## Acceptance Criteria
- [ ] Documentation matches source and tests.
- [ ] Examples are internally consistent and identify demo-only settings.
- [ ] All known limitations and unresolved assumptions are explicit.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
