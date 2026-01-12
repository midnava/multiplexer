public enum RoutingStrategy {
    BROADCAST, // Всем клиентам
    OWNER_ONLY // Только тому, кто первый ответил (или инициировал)
}