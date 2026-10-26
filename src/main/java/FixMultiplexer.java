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

    public void routeFromMarket(Message message, List<SessionID> clientSessions) {
        try {
            String msgType = message.getHeader().getString(35);
            MessageRouteConfig config = routes.get(msgType);

            if (config == null) {
                System.out.println(">>> No route config for msgType: " + msgType);
                return;
            }

            if (clientSessions.isEmpty()) {
                System.out.println(">>> Cannot broadcast: No clients connected yet!");
                return;
            }

            if (config.strategy() == RoutingStrategy.BROADCAST) {
                for (SessionID clientSession : clientSessions) {
                    System.out.println(">>> Routing to client: " + clientSession.getTargetCompID());
                    sendToSession(message, clientSession);
                }
            } else if (config.strategy() == RoutingStrategy.OWNER_ONLY) {
                String id = config.idExtractor().apply(message);
                SessionID owner = ownershipMap.get(id);
                if (owner != null) {
                    sendToSession(message, owner);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToSession(Message original, SessionID session) {
        try {
            // Создаем чистое сообщение того же типа
            Message copy = new Message();
            copy.fromString(original.toString(), null, false);

            // Очищаем заголовки, чтобы QFJ переписал их для новой сессии
            copy.getHeader().removeField(49); // Sender
            copy.getHeader().removeField(56); // Target
            copy.getHeader().removeField(34); // SeqNum
            copy.getHeader().removeField(52); // SendingTime

            Session.sendToTarget(copy, session);
        } catch (Exception e) {
            System.err.println("Failed to send to " + session + ": " + e.getMessage());
        }
    }

    public void handleClientResponse(Message message, SessionID clientSession, SessionID marketSession) {
        String msgType = getMsgType(message);
        MessageRouteConfig config = routes.get(msgType);

        if (config != null) {
            String id = config.idExtractor().apply(message);

            // Пытаемся занять ID. Если putIfAbsent вернул не null, значит владелец уже есть
            SessionID existingOwner = ownershipMap.putIfAbsent(id, clientSession);

            if (existingOwner != null && !existingOwner.equals(clientSession)) {
                System.out.println("!!! Rejecting Client " + clientSession.getTargetCompID() +
                        " - ID " + id + " already owned by " + existingOwner.getTargetCompID());
                sendReject(clientSession, id, "This RFQ is already being processed by another dealer");
                return; // Не шлем на рынок
            }
        }

        // Если мы владелец или правила нет — шлем на рынок
        sendMessage(message, marketSession);
    }

    private void sendReject(SessionID session, String id, String text) {
        // В реальности тут лучше создать BusinessMessageReject или QuoteStatusReport
        quickfix.fix44.Reject reject = new quickfix.fix44.Reject();
        reject.setField(new quickfix.field.Text(text));
        sendMessage(reject, session);
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