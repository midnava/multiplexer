# Configuration

## Current files

- `src/main/resources/multiplexer.cfg`: one upstream Initiator and two downstream Acceptor sessions, both configured on port 9880.
- `src/main/resources/clients.cfg`: three Initiator sessions from the multiplexer to client targets.
- `src/main/resources/market_emulator.cfg`: a market Acceptor on port 9876.

These files describe a demo with contradictory roles for the required production topology. Routing rules currently live in Java code in `MultiplexerRunner`, not in external configuration.

## Target configuration contract

Define separate downstream acceptor and upstream initiator sections with unique logical names, complete `SessionID` values, store/log settings, and explicit route rules. A route must identify message type, required identifier extraction, upstream destination, response behavior, and collision policy.

Configuration loading must produce an immutable validated object before any QuickFIX/J component starts. Reject duplicate logical names, duplicate session identities, invalid ports, missing required fields, unknown strategies, ambiguous routes, empty identifiers, and routes referencing unknown sessions.

Use isolated file stores per session in production/tests. Do not use inbound `SenderCompID`/`TargetCompID` as a destination lookup shortcut.
