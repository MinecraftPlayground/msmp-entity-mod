package dev.loat.msmp_entity_data.msmp.subscription;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages per-connection entity subscriptions for a single notification type.
 *
 * <p>Each notification (e.g. {@code dimension/changed}) owns its own
 * {@link SubscriptionManager} instance. Holds a mapping of
 * {@code connectionId -> Set<UUID>} where {@link #WILDCARD} means all entities.</p>
 *
 * <p>Thread-safe via {@link ConcurrentHashMap}.</p>
 */
public class SubscriptionManager {

    /**
     * Special UUID sentinel meaning "all entities".
     * Used when a client subscribes without specifying entity IDs.
     */
    public static final UUID WILDCARD = new UUID(0, 0);

    /** connectionId -> Set<entityUUID> */
    private final Map<String, Set<UUID>> subscriptions = new ConcurrentHashMap<>();

    /**
     * Subscribes a connection to this notification for the given entity UUIDs.
     * Pass {@link #WILDCARD} to receive notifications for all entities.
     *
     * @param connectionId The client connection ID
     * @param entityIds    The entity UUIDs to track, or a set containing {@link #WILDCARD}
     */
    public void subscribe(String connectionId, Set<UUID> entityIds) {
        subscriptions
            .computeIfAbsent(connectionId, k -> ConcurrentHashMap.newKeySet())
            .addAll(entityIds);
    }

    /**
     * Unsubscribes a connection from this notification for the given entity UUIDs.
     * If {@code entityIds} is empty, removes the connection entirely.
     *
     * @param connectionId The client connection ID
     * @param entityIds    The entity UUIDs to stop tracking, or empty to remove all
     */
    public void unsubscribe(String connectionId, Set<UUID> entityIds) {
        if (entityIds.isEmpty()) {
            subscriptions.remove(connectionId);
            return;
        }
        Set<UUID> tracked = subscriptions.get(connectionId);
        if (tracked != null) {
            tracked.removeAll(entityIds);
            if (tracked.isEmpty()) subscriptions.remove(connectionId);
        }
    }

    /**
     * Removes all subscriptions for a connection.
     * Should be called when a client disconnects.
     *
     * @param connectionId The client connection ID
     */
    public void removeAll(String connectionId) {
        subscriptions.remove(connectionId);
    }

    /**
     * Returns all connection IDs subscribed to this notification
     * for the given entity UUID (including wildcard subscribers).
     *
     * @param entityId The UUID of the entity that triggered the event
     * @return An unmodifiable set of connection IDs that should receive the notification
     */
    public Set<String> getSubscribers(UUID entityId) {
        Set<String> result = ConcurrentHashMap.newKeySet();
        for (Map.Entry<String, Set<UUID>> entry : subscriptions.entrySet()) {
            Set<UUID> tracked = entry.getValue();
            if (tracked.contains(WILDCARD) || tracked.contains(entityId)) {
                result.add(entry.getKey());
            }
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * Returns whether any connection is subscribed for the given entity UUID.
     *
     * @param entityId The UUID of the entity
     * @return {@code true} if at least one connection is subscribed
     */
    public boolean hasSubscribers(UUID entityId) {
        return !getSubscribers(entityId).isEmpty();
    }
}
