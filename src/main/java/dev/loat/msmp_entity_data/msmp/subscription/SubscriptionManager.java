package dev.loat.msmp_entity_data.msmp.subscription;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Manages per-connection entity event subscriptions.
 *
 * <p>Holds a mapping of {@code connectionId -> event -> Set<UUID>} that tracks
 * which clients want to receive notifications for which events on which entities.
 * Thread-safe via {@link ConcurrentHashMap}.</p>
 *
 * <p>A wildcard UUID ({@link #WILDCARD}) can be used to subscribe to an event
 * for <em>all</em> entities (e.g. all players).</p>
 */
public class SubscriptionManager {

    /**
     * Special UUID sentinel meaning "all entities".
     * Used when a client subscribes to an event without specifying entity IDs.
     */
    public static final UUID WILDCARD = new UUID(0, 0);

    /** connectionId -> event -> Set<entityUUID> */
    private final Map<String, Map<SubscriptionEvent, Set<UUID>>> subscriptions =
        new ConcurrentHashMap<>();

    /**
     * Subscribes a connection to the given event for the given entity UUIDs.
     * Pass {@link #WILDCARD} to subscribe to all entities.
     *
     * @param connectionId The client connection ID
     * @param event        The event to subscribe to
     * @param entityIds    The entity UUIDs to track (or {@link #WILDCARD})
     */
    public void subscribe(String connectionId, SubscriptionEvent event, Set<UUID> entityIds) {
        subscriptions
            .computeIfAbsent(connectionId, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(event, k -> ConcurrentHashMap.newKeySet())
            .addAll(entityIds);
    }

    /**
     * Unsubscribes a connection from the given event for the given entity UUIDs.
     * If no entity IDs are provided, removes the subscription entirely for that event.
     *
     * @param connectionId The client connection ID
     * @param event        The event to unsubscribe from
     * @param entityIds    The entity UUIDs to stop tracking, or empty to remove all
     */
    public void unsubscribe(String connectionId, SubscriptionEvent event, Set<UUID> entityIds) {
        Map<SubscriptionEvent, Set<UUID>> events = subscriptions.get(connectionId);
        if (events == null) return;

        if (entityIds.isEmpty()) {
            events.remove(event);
        } else {
            Set<UUID> tracked = events.get(event);
            if (tracked != null) {
                tracked.removeAll(entityIds);
                if (tracked.isEmpty()) events.remove(event);
            }
        }

        if (events.isEmpty()) subscriptions.remove(connectionId);
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
     * Returns all connection IDs that are subscribed to the given event
     * for the given entity UUID (including wildcard subscribers).
     *
     * @param event    The event to check
     * @param entityId The UUID of the entity that triggered the event
     * @return A set of connection IDs that should receive the notification
     */
    public Set<String> getSubscribers(SubscriptionEvent event, UUID entityId) {
        Set<String> result = ConcurrentHashMap.newKeySet();
        for (Map.Entry<String, Map<SubscriptionEvent, Set<UUID>>> entry : subscriptions.entrySet()) {
            Set<UUID> tracked = entry.getValue().get(event);
            if (tracked != null && (tracked.contains(WILDCARD) || tracked.contains(entityId))) {
                result.add(entry.getKey());
            }
        }
        return Collections.unmodifiableSet(result);
    }

    /**
     * Returns whether any connection is subscribed to the given event
     * for the given entity UUID.
     *
     * @param event    The event to check
     * @param entityId The UUID of the entity
     * @return {@code true} if at least one connection is subscribed
     */
    public boolean hasSubscribers(SubscriptionEvent event, UUID entityId) {
        return !getSubscribers(event, entityId).isEmpty();
    }
}
