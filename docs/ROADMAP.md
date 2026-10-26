# Implementation Roadmap

| Task | Title | Status | Dependencies | Expected result | Primary verification |
|---|---|---|---|---|---|
| 001 | Establish project package and build baseline | PARTIALLY_IMPLEMENTED | None | Clean Java 21 structure and explicit build/test baseline | `mvn test` |
| 002 | Define validated immutable configuration model | NOT_STARTED | 001 | Typed configuration loaded before startup | Unit tests |
| 003 | Separate QuickFIX/J session settings and stores | PARTIALLY_IMPLEMENTED | 002 | Independent downstream/upstream sessions | Configuration/integration tests |
| 004 | Implement downstream Acceptor lifecycle | PARTIALLY_IMPLEMENTED | 003 | Managed client acceptor and lifecycle registry | Integration test |
| 005 | Implement upstream Initiator lifecycle | PARTIALLY_IMPLEMENTED | 003 | Managed reconnecting upstream sessions | Integration test |
| 006 | Implement thread-safe session registry | PARTIALLY_IMPLEMENTED | 004, 005 | Explicit logical-to-live session ownership | Unit tests |
| 007 | Implement administrative-message isolation | PARTIALLY_IMPLEMENTED | 004, 005 | Admin traffic never enters business routing | Unit/integration tests |
| 008 | Implement client-to-upstream application routing | PARTIALLY_IMPLEMENTED | 002, 006, 007 | Configured ordered delivery to upstream | Unit/integration tests |
| 009 | Implement collision-safe correlation store | PARTIALLY_IMPLEMENTED | 002, 006 | Session-scoped ownership and expiry | Unit tests |
| 010 | Implement upstream-to-client response routing | PARTIALLY_IMPLEMENTED | 008, 009 | Responses delivered only to owner | Integration tests |
| 011 | Add safe copying and destination session headers | PARTIALLY_IMPLEMENTED | 008, 010 | No source sequence/header reuse or mutation | Unit tests |
| 012 | Add unavailable-session and backpressure policy | NOT_STARTED | 006, 008, 010 | Bounded non-blocking failure behavior | Unit tests |
| 013 | Add graceful shutdown and recovery limitations | NOT_STARTED | 004, 005, 009, 012 | Deterministic lifecycle and documented restart behavior | Integration tests |
| 014 | Add logging, masking, and health information | NOT_STARTED | 004, 005, 012 | Safe operational visibility | Unit tests |
| 015 | Build isolated FIX client/server integration harness | NOT_STARTED | 004, 005, 007 | Repeatable end-to-end verification | `mvn test` |
| 016 | Reconcile documentation and configuration examples | NOT_STARTED | 002-015 | Documentation matches implemented behavior | Review plus `mvn test` |
