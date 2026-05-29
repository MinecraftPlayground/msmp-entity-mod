package dev.loat.msmp_entity.msmp.subscription;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SubscriptionManager {

    private static final Map<String, SubscriptionManager> REGISTRY = new ConcurrentHashMap<>();

    public static SubscriptionManager get(String key) {
        return REGISTRY.computeIfAbsent(key, k -> new SubscriptionManager());
    }

    private final Set<UUID> subscriptions = ConcurrentHashMap.newKeySet();

    private SubscriptionManager() {}

    public void subscribe(Set<UUID> entityIds) {
        subscriptions.addAll(entityIds);
    }

    public void unsubscribe(Set<UUID> entityIds) {
        subscriptions.removeAll(entityIds);
    }

    public boolean isSubscribed(UUID entityId) {
        return subscriptions.contains(entityId);
    }

    public Set<UUID> getSubscriptions() {
        return Collections.unmodifiableSet(subscriptions);
    }
}
