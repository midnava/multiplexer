import quickfix.Application;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.SessionID;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiplexerApp extends MessageCracker implements Application {
    private final FixMultiplexer mux;
    private volatile SessionID marketSessionID;
    private final List<SessionID> clientSessions = new CopyOnWriteArrayList<>();

    public MultiplexerApp(FixMultiplexer mux) {
        this.mux = mux;
    }

    // Метод для ожидания в Main
    public boolean isReady() {
        return marketSessionID != null && clientSessions.size() >= 2;
    }

    @Override
    public void onLogon(SessionID sessionId) {
        if ("EXCHANGE_ID".equals(sessionId.getTargetCompID())) {
            this.marketSessionID = sessionId;
            System.out.println("[LOGON] Connected to MARKET: " + sessionId);
        } else {
            this.clientSessions.add(sessionId);
            System.out.println("[LOGON] Client connected: " + sessionId.getTargetCompID());
        }
    }

    @Override
    public void fromApp(Message message, SessionID sessionId) {
        if (sessionId.equals(marketSessionID)) {
            mux.routeFromMarket(message, clientSessions);
        } else {
            mux.handleClientResponse(message, sessionId, marketSessionID);
        }
    }

    @Override
    public void onLogout(SessionID s) {
        clientSessions.remove(s);
    }

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