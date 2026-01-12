import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class FixMultiplexer {
    // Храним: MessageID -> Client SessionID
    private final Map<String, SessionID> ownershipMap = new ConcurrentHashMap<>();
    private final Map<String, MessageRouteConfig> routes = new HashMap<>();

    private FixMultiplexer() {
    }

    public static class Builder {
        private final FixMultiplexer instance = new FixMultiplexer();

        public Builder addRule(String msgType, RoutingStrategy strategy, Function<Message, String> idExtractor) {
            instance.routes.put(msgType, new MessageRouteConfig(idExtractor, strategy));
            return this;
        }

        public FixMultiplexer build() {
            return instance;
        }
    }

    // Метод для вызова из Application.fromApp (рыночная сессия)
    public void routeFromMarket(Message message, List<SessionID> clientSessions) {
        String msgType = getMsgType(message);
        MessageRouteConfig config = routes.get(msgType);

        if (config == null) return;

        String id = config.idExtractor().apply(message);

        if (config.strategy() == RoutingStrategy.BROADCAST) {
            clientSessions.forEach(s -> sendMessage(message, s));
        } else if (config.strategy() == RoutingStrategy.OWNER_ONLY) {
            SessionID owner = ownershipMap.get(id);
            if (owner != null) {
                sendMessage(message, owner);
            }
        }
    }

    // Метод для вызова из Application.fromApp (клиентская сессия)
    public void handleClientResponse(Message message, SessionID clientSession, SessionID marketSession) {
        String msgType = getMsgType(message);
        MessageRouteConfig config = routes.get(msgType);

        if (config != null) {
            String id = config.idExtractor().apply(message);
            // Пытаемся стать владельцем этого ID (RFQ/Quote ID)
            ownershipMap.putIfAbsent(id, clientSession);
        }

        // Пересылаем на рынок
        sendMessage(message, marketSession);
    }

    private String getMsgType(Message msg) {
        try {
            return msg.getHeader().getString(35);
        } catch (Exception e) {
            return "";
        }
    }

    private void sendMessage(Message msg, SessionID session) {
        try {
            Session.sendToTarget(msg, session);
        } catch (Exception ignored) {
        }
    }
}