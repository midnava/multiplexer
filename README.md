```mermaid
sequenceDiagram
    participant C as FIX Client
    participant P as Platform
    participant D as Dealer UI

    C->>P: QuoteRequest (35=R)
    Note over P: RFQ.status = QUOTE_REQUESTED<br/>Quote.status = none yet<br/>Quote.price = —

    D->>P: Submit price P
    P-->>C: Quote (35=S), 537=1, price=P — Firm
    Note over P: RFQ.status = QUOTED<br/>Quote.status = QUOTE_ON_THE_WIRE<br/>Quote.price = P

    alt Client accepts Firm within 5 seconds
        C->>P: QuoteResponse (35=AJ), 694=1 — Hit/Lift
        P-->>C: ExecutionReport (35=8) — trade executed
        Note over P: RFQ.status = DONE<br/>Quote.status = DONE<br/>Quote.price = agreed price P
    else 5 seconds have elapsed
        P-->>C: Quote (35=S), 537=0, price=P — Indicative / Subject
        Note over P: RFQ.status = QUOTED<br/>Quote.status = QUOTE_SUBJECT<br/>Quote.price = P

        C->>P: QuoteResponse (35=AJ), 694=1 — Hit/Lift
        Note over P: RFQ.status = QUOTED<br/>Quote.status = TRADE_PENDING_LAST_LOOK<br/>Record the accepted QuoteID and price P

        P->>D: Request Last Look at price P

        alt Dealer accepts
            D->>P: Dealer accepts order
            Note over P: RFQ.status = QUOTED<br/>Quote.status = DEALER_ACCEPTED_ORDER<br/>Quote.price = P

            P-->>C: ExecutionReport (35=8) — trade executed
            Note over P: RFQ.status = DONE<br/>Quote.status = DONE<br/>Quote.price = agreed price P
        else Dealer rejects
            D->>P: Dealer rejects order
            P-->>C: Rejection result according to the agreed FIX profile
            Note over P: Quote.status = REJECTED<br/>RFQ.status = QUOTED if the RFQ remains open<br/>Otherwise RFQ.status = DONE
        end
    end
```
	
```mermaid	
sequenceDiagram
    participant C as Client
    participant P as Platform
    participant D as Dealer

    C->>P: QuoteRequest (35=R)
    D->>P: Price P
    P-->>C: Quote (35=S, 537=1) — Firm

    Note over P,C: After 5 seconds: update the Quote, keeping the price
    P-->>C: Quote (35=S, 537=0) — Indicative / Subject

    C->>P: QuoteResponse (35=AJ, 694=1) — Hit/Lift
    P->>D: Last Look for the recorded QuoteID and price

    alt Dealer accepts
        D->>P: Acceptance
        P-->>C: ExecutionReport (35=8) — execution result
    else Dealer rejects
        D->>P: Rejection
        P-->>C: Rejection message according to the agreed FIX profile
    end	
```
	
### Rfq.status:	
	
| Status | Description | Price |
|---|---|---|
| `QUOTE_REQUESTED` | The client submitted an RFQ; there is no active quote yet. | Not set |
| `QUOTED` | The dealer published a quote. This status remains while the request is open, including during Subject and Last Look. | Current quote price |
| `DONE` | The RFQ is complete: a trade was executed or the request was closed without a trade. Store the completion reason separately, for example `TRADED`, `REJECTED`, `PASSED`, or `EXPIRED`. | Trade price, if a trade was executed |	


### Quote.staus
| Status | Description | Price |
|---|---|---|
| `QUOTE_ON_THE_WIRE` | An active firm quote. If the client accepts it, execution proceeds without Last Look. | Most recently published price |
| `QUOTE_SUBJECT` | An active indicative quote. If the client accepts it, the dealer’s Last Look process begins. | Most recently published price |
| `TRADE_PENDING_LAST_LOOK` | The client accepted the Subject quote; the dealer must accept or reject the request. Record the accepted price and `QuoteID`. | Price accepted by the client |
| `DEALER_ACCEPTED_ORDER` | The dealer accepted the request after Last Look; execution is pending completion. | Agreed price |
| `DONE` | The quote has completed with an executed trade. | Executed price |
| `REJECTED` | The dealer rejected Last Look for this quote version. If the RFQ is still open, the dealer may publish a new quote version. | Price of the rejected quote |


```mermaid
%%{init: {"themeVariables": {"fontSize": "15px"}, "flowchart": {"nodeSpacing": 90, "rankSpacing": 120, "curve": "basis"}}}%%
flowchart LR
    TP["T-Pricer<br/>(Customer FIX)"]
    GW["T-Pricer FIX Gateway"]
    SE["StateEngine"]
    AS["Algo Streamer"]
    AC["Algo Control"]
    UI["NEO UI"]

    TP -->|"FIX: QuoteRequest (35=R)<br/>QuoteResponse (35=AJ)"| GW
    GW -->|"FIX: Quote (35=S)<br/>ExecutionReport (35=8)<br/>Reject"| TP

    GW -->|"Kafka: RFQ / Quote state changes"| SE

    SE -->|"Quote, LastLook, Reject<br/>(Dealer API)"| GW

    SE -->|"RFQ / Quote state"| UI
    UI -->|"Streaming, Click-Off, Pause<br/>Manual price, Charge<br/>LastLook, Reject"| SE

    AC -->|"canQuote, autoStreaming<br/>autoLastLook, hedge"| SE

    SE -->|"Price stream request"| AS
    AS -->|"midPrice, charge"| SE
```
