# Project Overview

## Purpose

`fix-multiplexer` is intended to accept FIX client sessions, maintain an upstream FIX connection, route application messages using validated external configuration, and return upstream responses to the originating client. It uses Java 21 and QuickFIX/J.

## Verified repository state

- `pom.xml` declares Java release 21, QuickFIX/J 2.3.2, SLF4J Simple, JUnit Jupiter, and Surefire.
- Production code is in the default package under `src/main/java`.
- `MultiplexerRunner` starts a market emulator Acceptor, a multiplexer Initiator, and a client Initiator. This is a demo topology, not the required production topology.
- `MultiplexerApp` identifies the market by hard-coded `TargetCompID` and considers readiness to mean two clients plus the market are logged on.
- `FixMultiplexer` has in-memory routing and ownership maps, supports message-type rules, and copies messages by serializing and parsing them before sending.
- `FixMultiplexerBuilder` is separate, incomplete, and unused.
- Configuration is three QuickFIX/J `.cfg` files. There are no application configuration files, tests, README, docs, or AGENTS.md in the repository.

## Completion terminology

The roadmap uses `COMPLETED` only for behavior verified in source and tests. Existing prototype behavior that is unsafe, hard-coded, or incomplete is `PARTIALLY_IMPLEMENTED`.

## Non-goals

No database, Kafka, Docker, REST API, Spring Boot, clustering, or web interface is required by the current project brief.
