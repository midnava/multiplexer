import quickfix.Message;
import quickfix.SessionID;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class FixMultiplexerBuilder {
    private final Map<String, SessionID> messageOwners = new ConcurrentHashMap<>();
    private final Map<Class<? extends Message>, MessageRouteConfig> routes;

    private FixMultiplexerBuilder(Builder builder) {
        this.routes = builder.routes;
    }

    public static class Builder {
        private final Map<Class<? extends Message>, MessageRouteConfig> routes = new HashMap<>();

        public Builder routeMessage(Class<? extends Message> msgClass, 
                                    Function<Message, String> idExtractor,
                                    RoutingStrategy strategy) {
            routes.put(msgClass, new MessageRouteConfig(idExtractor, strategy));
            return this;
        }

        public FixMultiplexerBuilder build() {
            return new FixMultiplexerBuilder(this);
        }
    }
}