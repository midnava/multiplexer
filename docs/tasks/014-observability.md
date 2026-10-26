# Task 014: Add logging, masking, and health information

## Status
NOT_STARTED

## Context
Production classes use `System.out`, stack traces, and full message output; this can leak sensitive FIX fields and gives no health summary.

## Goal
Provide safe structured diagnostics and operational session health.

## Current State
`FixMultiplexer`, `MultiplexerApp`, and `MarketEmulatorApp` print raw messages/identifiers; no health model exists.

## Requirements
- Replace ad hoc output with the existing SLF4J API.
- Mask configurable sensitive fields and avoid raw message logging by default.
- Report session state, route failures, queue depth, and correlation counts.
- Keep health information in-process; no web interface is required.

## FIX Behavior
Diagnostics may identify MsgType and session logical name but must not alter FIX messages or forward admin traffic.

## Files in Scope
Logging/health classes, configuration, tests, docs.

## Out of Scope
REST APIs, dashboards, and external telemetry systems.

## Design Constraints
Thread-safe health state; no callback blocking.

## Error Handling
Logging failure must not break routing; masking configuration errors fail startup if unsafe.

## Testing
Assert sensitive values are masked and lifecycle health transitions are visible.

## Acceptance Criteria
- [ ] Raw sensitive fields are not logged by default.
- [ ] Session and delivery health is queryable in-process.
- [ ] Tests verify masking and state transitions.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
