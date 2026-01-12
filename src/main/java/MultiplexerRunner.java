import quickfix.*;

public class MultiplexerRunner {
    public static void main(String[] args) throws Exception {
        // 1. Инициализируем наш Мультиплексор
        FixMultiplexer mux = new FixMultiplexer.Builder()
                .addRule("R", RoutingStrategy.BROADCAST, msg -> getTag(msg, 131))
                .addRule("S", RoutingStrategy.OWNER_ONLY, msg -> getTag(msg, 131))
                .build();
        MultiplexerApp muxApp = new MultiplexerApp(mux);

        // 2. Инициализируем Эмулятор Рынка
        MarketEmulatorApp marketApp = new MarketEmulatorApp();

        // 3. Настраиваем инфраструктуру
        MessageStoreFactory store = new MemoryStoreFactory();
        LogFactory log = new ScreenLogFactory(true, true, true);
        DefaultMessageFactory factory = new DefaultMessageFactory();

        // --- ЗАПУСК РЫНКА (Acceptor на 9876) ---
        SessionSettings mktEmuSettings = new SessionSettings("market_emulator.cfg");
        ThreadedSocketAcceptor marketEmulator = new ThreadedSocketAcceptor(
                marketApp, store, mktEmuSettings, log, factory);
        marketEmulator.start();

        // --- ЗАПУСК МУЛЬТИПЛЕКСОРА (Initiator к рынку + Acceptor для клиентов на 9880) ---
        SessionSettings muxToMarketSettings = new SessionSettings("market.cfg");
        ThreadedSocketInitiator muxInitiator = new ThreadedSocketInitiator(
                muxApp, store, muxToMarketSettings, log, factory);

        SessionSettings muxToClientSettings = new SessionSettings("clients.cfg");
        ThreadedSocketAcceptor muxAcceptor = new ThreadedSocketAcceptor(
                muxApp, store, muxToClientSettings, log, factory);

        muxInitiator.start();
        muxAcceptor.start();

        System.out.println(">>> Вся цепочка запущена.");
        System.out.println(">>> Рынок слушает 9876");
        System.out.println(">>> Мултиплексор слушает клиентов на 9880");

        Thread.currentThread().join();
    }

    private static String getTag(Message msg, int tag) {
        try {
            return msg.getString(tag);
        } catch (FieldNotFound e) {
            return "";
        }
    }
}