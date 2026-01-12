import quickfix.Message;

import java.util.function.Function;

public record MessageRouteConfig(Function<Message, String> idExtractor, RoutingStrategy strategy) {
}