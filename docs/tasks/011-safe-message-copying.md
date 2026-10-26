# Task 011: Add safe copying and destination session headers

## Status
PARTIALLY_IMPLEMENTED

## Context
`sendToSession` serializes and reparses a `Message`, removes selected headers, and ignores the possibility of parse loss or mutation elsewhere.

## Goal
Create destination-safe copies without mutating inbound QuickFIX/J messages or forwarding source session fields.

## Current State
`FixMultiplexer.sendToSession` removes tags 49, 56, 34, and 52 from a parsed copy before `Session.sendToTarget`.

## Requirements
- Define and test the copy mechanism for header, body, and repeating groups.
- Remove/avoid source `34`, `49`, `52`, `56` and other session-managed fields.
- Preserve business fields exactly.
- Let QuickFIX/J set destination identity and sequence values.

## FIX Behavior
Messages retain their application `35` and business fields; destination QuickFIX/J session supplies session headers and sequence number.

## Files in Scope
Message-copy utility, routers, tests.

## Out of Scope
Dictionary redesign and business transformations.

## Design Constraints
No inbound Message mutation; independent FIX sequence numbers.

## Error Handling
Copy/parse failure is a bounded failed delivery with diagnostic context; original message remains usable.

## Testing
Test fields/groups, source header absence, and original-message immutability.

## Acceptance Criteria
- [ ] Copies preserve required application content.
- [ ] Source session headers are not reused.
- [ ] Original instances are unchanged.

## Verification
Run `mvn test`. Report files, decisions, tests/results, and unresolved risks.
