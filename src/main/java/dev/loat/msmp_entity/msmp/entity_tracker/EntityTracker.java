package dev.loat.msmp_entity.msmp.entity_tracker;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Holds a set of tracked entity UUIDs for a specific notification path.
 *
 * <p>Instances are keyed by the notification path they belong to
 * (e.g. {@code "entity:notification/position/changed"}) and retrieved via {@link #get(String)}.
 * The same instance is shared across all components that participate in that notification.</p>
 *
 * <p>All operations are thread-safe.</p>
 */
public final class EntityTracker {

    private static final Map<String, EntityTracker> REGISTRY = new ConcurrentHashMap<>();

    /**
     * Returns the {@link EntityTracker} for the given notification path,
     * creating it if it does not yet exist.
     *
     * @param path The notification path (e.g. {@code "entity:notification/position/changed"})
     * @return The shared {@link EntityTracker} instance for that path
     */
    public static EntityTracker get(String path) {
        return REGISTRY.computeIfAbsent(path, k -> new EntityTracker());
    }

    private final Set<UUID> tracked = ConcurrentHashMap.newKeySet();

    private EntityTracker() {}

    /**
     * Adds the given UUIDs to the tracked set.
     *
     * @param entityIds The UUIDs to start tracking
     */
    public void add(Set<UUID> entityIds) {
        tracked.addAll(entityIds);
    }

    /**
     * Removes the given UUIDs from the tracked set.
     *
     * @param entityIds The UUIDs to stop tracking
     */
    public void remove(Set<UUID> entityIds) {
        tracked.removeAll(entityIds);
    }

    /**
     * Returns {@code true} if the given UUID is currently being tracked.
     *
     * @param entityId The UUID to check
     * @return {@code true} if tracked, {@code false} otherwise
     */
    public boolean contains(UUID entityId) {
        return tracked.contains(entityId);
    }

    /**
     * Returns an unmodifiable view of all currently tracked UUIDs.
     *
     * @return The set of tracked UUIDs
     */
    public Set<UUID> entities() {
        return Collections.unmodifiableSet(tracked);
    }
}
