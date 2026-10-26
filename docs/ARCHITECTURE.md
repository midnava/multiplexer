# Architecture

## Target topology

```text
FIX clients -> downstream Acceptor -> session registry -> application router -> upstream Initiator -> FIX server
FIX server  -> upstream Initiator -> correlation/router -> owning downstream client
```

QuickFIX/J owns transport, logon/logout, validation, sequence numbers, and administrative messages for each independent `SessionID`. The multiplexer owns application routing and correlation only.

## Components

- Downstream session manager: creates and tracks Acceptor sessions and lifecycle state.
- Upstream session manager: creates and tracks one or more Initiator sessions and reconnect behavior.
- Immutable configuration: loads and validates session and routing definitions before startup.
- Session registry: maps configured logical roles to live QuickFIX/J sessions; it must not infer destination identity from inbound header fields.
- Application router: selects upstream destinations for client application messages.
- Correlation store: maps collision-safe request keys to client session ownership and lifecycle state.
- Response router: sends upstream application responses only to the resolved client.
- Operational layer: bounded asynchronous delivery, logging, health state, and graceful shutdown.

## Threading and ordering

QuickFIX/J callbacks must perform bounded, non-blocking work. If delivery is asynchronous, each destination needs a bounded queue and a single ordered drain; overload and disconnected destinations must have explicit outcomes. Never mutate inbound `Message` instances. Destination sends must let QuickFIX/J generate destination headers and sequence numbers.

## Current risks

The prototype shares a map-backed `MemoryStoreFactory` across independently created acceptor/initiator settings, hard-codes roles, swallows send errors, has no lifecycle handles, and may broadcast upstream messages to every client. It also has no durable recovery policy for correlation state.
