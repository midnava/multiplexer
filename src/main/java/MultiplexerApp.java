import quickfix.Application;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.SessionID;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiplexerApp extends MessageCracker implements Application {
    private final FixMultiplexer multiplexer;
    // Сессия рынка
    private SessionID marketSession;
    // Список активных клиентских сессий
    private final List<SessionID> clientSessions = new CopyOnWriteArrayList<>();

    public MultiplexerApp(FixMultiplexer multiplexer) {
        this.multiplexer = multiplexer;
    }

    // --- Методы QuickFIX/J ---

    @Override
    public void onLogon(SessionID sessionId) {
        // Простая логика: если в конфиге сессии указано "MARKET", сохраняем её отдельно
        if (sessionId.getSenderCompID().equals("MARKET_SIDE")) {
            this.marketSession = sessionId;
        } else {
            this.clientSessions.add(sessionId);
        }
    }

    @Override
    public void onLogout(SessionID sessionId) {
        clientSessions.remove(sessionId);
    }

    @Override
    public void fromApp(Message message, SessionID sessionId) {
        if (sessionId.equals(marketSession)) {
            // Сообщение пришло от рынка -> маршрутизируем клиентам
            multiplexer.routeFromMarket(message, clientSessions);
        } else {
            // Сообщение от клиента -> регистрируем владельца и шлем на рынок
            multiplexer.handleClientResponse(message, sessionId, marketSession);
        }
    }

    // Остальные методы (toApp, fromAdmin и т.д.) оставляем пустыми или для логов
    @Override
    public void toApp(Message m, SessionID s) {
    }

    @Override
    public void fromAdmin(Message m, SessionID s) {
    }

    @Override
    public void toAdmin(Message m, SessionID s) {
    }

    @Override
    public void onCreate(SessionID s) {
    }
}