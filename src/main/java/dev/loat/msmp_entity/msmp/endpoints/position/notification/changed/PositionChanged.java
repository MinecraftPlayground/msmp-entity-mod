package dev.loat.msmp_entity.msmp.endpoints.position.notification.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.config.Config;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.subscription.SubscriptionManager;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;


/**
 * Registers the {@code entity:notification/position/changed} MSMP notification and its
 * tick-based polling loop.
 *
 * <p>On every server tick the counter is incremented. Once it reaches
 * {@code intervalTicks}, every subscribed entity is checked. A notification is dispatched
 * only if the entity has moved at least {@code blockDelta} blocks since its last recorded
 * position.</p>
 *
 * <p>Setting {@code blockDelta} to {@code 0.0} notifies on any movement.</p>
 *
 * <p>Example notification payload:</p>
 * <pre><code>
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "from":   [128.5, 64.0, -32.3],
 *   "to":     [131.2, 64.0, -29.7]
 * }
 * </code></pre>
 */
public class PositionChanged {

    /** Subscription key used with the global {@link SubscriptionManager}. */
    public static final String SUBSCRIPTION_KEY = "entity:notification/position/changed";

    /**
     * Last recorded positions per entity UUID.
     * Populated on first poll after subscription; updated after each dispatched notification.
     * Cleared explicitly on unsubscribe via {@link dev.loat.msmp_entity.msmp.endpoints.position.changed.PositionChangedRemove}.
     */
    public static final Map<UUID, double[]> LAST_POSITIONS = new ConcurrentHashMap<>();

    private PositionChanged() {}

    /**
     * Registers the {@code entity:notification/position/changed} notification and the
     * server-tick polling loop.
     *
     * @param namespace  The namespace to register this notification under
     * @param msmpServer Supplier for the running {@link MSMPServer}; may return {@code null}
     *                   when no server is active
     */
    public static void register(MSMPNamespace namespace, Supplier<MSMPServer> msmpServer) {
        MSMPNotification<PositionChangedPayload> notification = namespace.notification(
            "position/changed",
            PositionChangedPayload.SCHEMA,
            "Fired when a subscribed entity moves at least blockDelta blocks (checked every intervalTicks ticks)"
        );

        SubscriptionManager manager = SubscriptionManager.get(SUBSCRIPTION_KEY);
        int[] tickCounter = {0};

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter[0]++;

            int interval = Config.getConfig().position.notification.intervalTicks;
            if (interval < 1) interval = 1;

            if (tickCounter[0] % interval != 0) return;

            MSMPServer msmp = msmpServer.get();
            if (msmp == null) return;

            double blockDelta = Config.getConfig().position.notification.blockDelta;

            Set<UUID> subscriptions = manager.getSubscriptions();
            if (subscriptions.isEmpty()) return;

            for (UUID uuid : subscriptions) {
                Entity entity = findEntity(server, uuid);
                if (entity == null) continue;

                double[] last = LAST_POSITIONS.get(uuid);
                double cx = entity.getX();
                double cy = entity.getY();
                double cz = entity.getZ();

                if (last == null) {
                    // First poll after subscription — record position, don't notify yet
                    LAST_POSITIONS.put(uuid, new double[]{cx, cy, cz});
                    continue;
                }

                double dx = cx - last[0];
                double dy = cy - last[1];
                double dz = cz - last[2];
                double distSq = dx * dx + dy * dy + dz * dz;

                if (distSq < blockDelta * blockDelta) continue;

                PositionChangedPayload payload = new PositionChangedPayload(
                    EntityResolver.toEntityRef(entity),
                    List.of(last[0], last[1], last[2]),
                    List.of(cx, cy, cz)
                );

                msmp.send(notification, payload);
                LAST_POSITIONS.put(uuid, new double[]{cx, cy, cz});
            }
        });
    }

    /**
     * Searches all loaded levels for the entity with the given UUID.
     * Uses the player list as a fast path.
     *
     * @param server The running {@link MinecraftServer}
     * @param uuid   The UUID to look up
     * @return The found {@link Entity}, or {@code null} if not loaded
     */
    private static Entity findEntity(MinecraftServer server, UUID uuid) {
        Entity entity = server.getPlayerList().getPlayer(uuid);
        if (entity != null) return entity;

        for (ServerLevel level : server.getAllLevels()) {
            entity = level.getEntity(uuid);
            if (entity != null) return entity;
        }
        return null;
    }
}
