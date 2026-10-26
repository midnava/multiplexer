import quickfix.*;
import quickfix.field.QuoteID;
import quickfix.field.QuoteReqID;
import quickfix.fix44.Quote;
import quickfix.fix44.QuoteRequest;

public class MultiplexerRunner {
    public static void main(String[] args) throws Exception {
        FixMultiplexer mux = new FixMultiplexer.Builder()
                .addRule("R", RoutingStrategy.BROADCAST, m -> getTag(m, 131))
                .addRule("S", RoutingStrategy.OWNER_ONLY, m -> getTag(m, 131))
                .build();

        MultiplexerApp muxApp = new MultiplexerApp(mux);
        MarketEmulatorApp marketApp = new MarketEmulatorApp();

        MessageStoreFactory store = new MemoryStoreFactory();
        LogFactory log = new ScreenLogFactory(true, true, true);
        DefaultMessageFactory factory = new DefaultMessageFactory();

        // Запуск трех сторон
        new ThreadedSocketAcceptor(marketApp, store, new SessionSettings("market_emulator.cfg"), log, factory).start();
        new ThreadedSocketInitiator(muxApp, store, new SessionSettings("multiplexer.cfg"), log, factory).start();
        new ThreadedSocketInitiator(muxApp, store, new SessionSettings("clients.cfg"), log, factory).start();

        // ОЖИДАНИЕ ГОТОВНОСТИ
        System.out.println("Waiting for logons...");
        while (!muxApp.isReady()) {
            Thread.sleep(100);
        }

        // СЦЕНАРИЙ
        System.out.println("\n--- STEP 1: Market sends RFQ (Broadcast) ---");
        SessionID marketToMux = new SessionID("FIX.4.4", "EXCHANGE_ID", "MY_MUX_ID");
        QuoteRequest rfq = new QuoteRequest(new QuoteReqID("RFQ_2026"));
        Session.sendToTarget(rfq, marketToMux);

        Thread.sleep(1000);

        System.out.println("\n--- STEP 2: Client A responds FIRST ---");
        SessionID clientA = new SessionID("FIX.4.4", "CLIENT_A", "MUX_SERVER");
        Quote quoteA = new Quote(new QuoteID("Q1"));
        quoteA.set(new QuoteReqID("RFQ_2026"));
        Session.sendToTarget(quoteA, clientA);

        Thread.sleep(200);

        System.out.println("\n--- STEP 3: Client B responds SECOND ---");
        SessionID clientB = new SessionID("FIX.4.4", "CLIENT_B", "MUX_SERVER");
        Quote quoteB = new Quote(new QuoteID("Q2"));
        quoteB.set(new QuoteReqID("RFQ_2026"));
        Session.sendToTarget(quoteB, clientB);

        System.out.println("\nDone. Press Enter to exit.");
        System.in.read();
    }

    private static String getTag(Message m, int t) {
        try {
            return m.getString(t);
        } catch (Exception e) {
            return "";
        }
    }
}