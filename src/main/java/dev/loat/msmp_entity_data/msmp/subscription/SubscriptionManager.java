package dev.loat.msmp_entity_data.msmp.subscription;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SubscriptionManager {

    /** connectionId -> Set<entityUUID> */
    private final Map<Integer, Set<UUID>> subscriptions = new ConcurrentHashMap<>();

    public void subscribe(int connectionId, Set<UUID> entityIds) {
        if (entityIds.isEmpty()) return;
        subscriptions
            .computeIfAbsent(connectionId, k -> ConcurrentHashMap.newKeySet())
            .addAll(entityIds);
    }

    public void unsubscribe(int connectionId, Set<UUID> entityIds) {
        if (entityIds.isEmpty()) return;
        Set<UUID> tracked = subscriptions.get(connectionId);
        if (tracked != null) {
            tracked.removeAll(entityIds);
            if (tracked.isEmpty()) subscriptions.remove(connectionId);
        }
    }

    public void removeAll(int connectionId) {
        subscriptions.remove(connectionId);
    }

    public Set<Integer> getSubscribers(UUID entityId) {
        Set<Integer> result = ConcurrentHashMap.newKeySet();
        for (Map.Entry<Integer, Set<UUID>> entry : subscriptions.entrySet()) {
            if (entry.getValue().contains(entityId)) {
                result.add(entry.getKey());
            }
        }
        return Collections.unmodifiableSet(result);
    }

    public boolean hasSubscribers(UUID entityId) {
        return !getSubscribers(entityId).isEmpty();
    }
}
