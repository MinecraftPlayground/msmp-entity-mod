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
 * {@link dev.loat.msmp_entity.config.files.MSMPEntityConfigFile#positionNotificationIntervalTicks},
 * every subscribed entity is checked. A notification is dispatched only if the entity has
 * moved at least
 * {@link dev.loat.msmp_entity.config.files.MSMPEntityConfigFile#positionNotificationBlockDelta}
 * blocks since its last recorded position.</p>
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

    /**
     * Last recorded positions per entity UUID.
     * Populated when an entity is subscribed; updated after each dispatched notification.
     * Cleared when an entity is unsubscribed.
     */
    static final Map<UUID, double[]> LAST_POSITIONS = new ConcurrentHashMap<>();
    static int tickCounter = 0;

    private PositionChanged() {}

    /**
     * Registers the {@code entity:notification/position/changed} notification and the
     * server-tick polling loop.
     *
     * @param namespace  The namespace to register this notification under
     * @param msmpServer Supplier for the running {@link MSMPServer}; may return {@code null}
     *                   when no server is active
     */
    public static void register(
        MSMPNamespace namespace,
        Supplier<MSMPServer> msmpServer
    ) {
        MSMPNotification<PositionChangedPayload> notification = namespace.notification(
            "position/changed",
            PositionChangedPayload.SCHEMA,
            "Fired when a subscribed entity moves at least blockDelta blocks (checked every intervalTicks ticks)"
        );

        SubscriptionManager manager = SubscriptionManager.get("entity:notification/position/changed");

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            PositionChanged.tickCounter++;

            int interval = Config.getConfig().position.notification.intervalTicks;
            double blockDelta = Config.getConfig().position.notification.blockDelta;
            
            if (interval < 1) {interval = 1;} // guard against bad config values
            if (PositionChanged.tickCounter % interval != 0) return;

            Set<UUID> subscriptions = manager.getSubscriptions();

            for (UUID uuid : subscriptions) {
                Entity entity = PositionChanged.findEntity(server, uuid);
                if (entity == null) continue;

                double[] last = LAST_POSITIONS.get(uuid);
                double currentX = entity.getX();
                double currentY = entity.getY();
                double currentZ = entity.getZ();

                if (last == null) {
                    // first tick after subscription - record position, don't notify yet
                    LAST_POSITIONS.put(uuid, new double[]{currentX, currentY, currentZ});
                    continue;
                }

                double dx = currentX - last[0];
                double dy = currentY - last[1];
                double dz = currentZ - last[2];
                double distSq = dx * dx + dy * dy + dz * dz;

                if (distSq < blockDelta * blockDelta) continue;

                PositionChanged.dispatch(msmpServer, notification, entity, last, new double[]{currentX, currentY, currentZ});
                LAST_POSITIONS.put(uuid, new double[]{currentX, currentY, currentZ});
            }
        });
    }

    public static void dispatch(
        Supplier<MSMPServer> msmpServer,
        MSMPNotification<PositionChangedPayload> notification,
        Entity entity,
        double[] from,
        double[] to
    ) {
        MSMPServer server = msmpServer.get();
        if (server == null) return;

        PositionChangedPayload payload = new PositionChangedPayload(
            EntityResolver.toEntityRef(entity),
            List.of(from[0], from[1], from[2]),
            List.of(to[0], to[1], to[2])
        );

        server.send(notification, payload);
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
