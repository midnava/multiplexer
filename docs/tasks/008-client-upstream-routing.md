# Task 008: Implement client-to-upstream application routing

## Status
PARTIALLY_IMPLEMENTED

## Context
`FixMultiplexer.handleClientResponse` forwards every recognized client message to one market session and routes are hard-coded by MsgType.

## Goal
Route client application messages to configured upstream destinations.

## Current State
Rules for `35=R` and `35=S` exist in `MultiplexerRunner`; destination selection is not configured and send failures are swallowed.

## Requirements
- Select destination using immutable configuration and registry.
- Validate required identifiers before routing.
- Preserve order per destination.
- Do not broadcast unless explicitly configured.
- Do not block QuickFIX/J callbacks.

## FIX Behavior
Client application messages flow downstream to upstream; destination session generates admin header fields and sequence numbers.

## Files in Scope
Router, configuration adapter, registry integration, tests.

## Out of Scope
Upstream response correlation and optional broadcast features.

## Design Constraints
Safe message copying; bounded asynchronous work if used; no source sequence reuse.

## Error Handling
Missing route/identifier or unavailable upstream produces a defined rejection or failure metric; overload does not crash or block callbacks.

## Testing
Unit-test route selection and ordering; integration-test delivery to a test upstream.

## Acceptance Criteria
- [ ] Valid messages reach the configured upstream only.
- [ ] Missing/invalid routes are handled deterministically.
- [ ] Per-destination order is preserved.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
