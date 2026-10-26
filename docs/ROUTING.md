# Routing Contract

Only application messages received in `fromApp` are eligible for business routing. Admin messages (`35=A, D, 5, 0, 1, 2, 3, 4`) remain owned by their QuickFIX/J session and are never forwarded by the multiplexer.

## Directions

- Client to upstream: choose a configured upstream route, copy the message, and send it through that destination session.
- Upstream to client: resolve the request correlation key and send only to the owning downstream session.
- Missing route, missing correlation, unavailable destination, malformed required identifier, or ambiguous configuration must produce a defined rejection/metric/log outcome and must not block the callback thread.

## Identity and correlation

Client ownership is scoped by downstream `SessionID`; a bare `ClOrdID` or `QuoteReqID` is not globally unique. The key must include the configured business identifier and source session (and message type where required). Correlation creation must be atomic, reject collisions between different clients, and define expiry and cleanup.

## FIX fields

Rules must name the relevant `35=MsgType` and required business identifiers such as `11=ClOrdID`, `131=QuoteReqID`, or another explicitly configured field. `34`, `49`, `52`, and `56` are session-managed and must not be forwarded as source-session values. Responses must not be broadcast unless a route explicitly declares broadcast semantics.

## Prototype mapping

The verified prototype has `35=R` broadcast using tag 131 and `35=S` owner-only using tag 131. This is not yet immutable external configuration, does not define upstream destination selection, and does not provide a complete response-correlation contract.
