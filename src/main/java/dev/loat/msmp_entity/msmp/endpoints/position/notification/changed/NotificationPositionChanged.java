package dev.loat.msmp_entity.msmp.endpoints.position.notification.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.config.Config;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
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
 * <p>Checks every {@code intervalTicks} ticks whether a tracked entity has moved
 * at least {@code blockDelta} blocks since its last recorded position.</p>
 */
public class NotificationPositionChanged {

    public static final String TRACKER_KEY = "entity:notification/position/changed";

    /**
     * Last recorded positions per entity UUID.
     * Reset on add (fresh baseline); cleared on remove (memory cleanup).
     */
    public static final Map<UUID, double[]> LAST_POSITIONS = new ConcurrentHashMap<>();

    private NotificationPositionChanged() {}

    public static void register(MSMPNamespace namespace, Supplier<MSMPServer> msmpServer) {
        
        MSMPNotification<NotificationPositionChangedPayload> notification = namespace.notification("position/changed")
            .description("Fired when a tracked entity moves at least blockDelta blocks (checked every intervalTicks ticks)")
            .responseSchema(NotificationPositionChangedPayload.SCHEMA)
            .register();

        EntityTracker tracker = EntityTracker.get(TRACKER_KEY);
        int[] tickCounter = {0};

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter[0]++;

            int interval = Config.getConfig().position.notification.intervalTicks;
            if (interval < 1) interval = 1;

            if (tickCounter[0] % interval != 0) return;

            MSMPServer msmp = msmpServer.get();
            if (msmp == null) return;

            double blockDelta = Config.getConfig().position.notification.blockDelta;

            Set<UUID> entities = tracker.entities();
            if (entities.isEmpty()) return;

            for (UUID uuid : entities) {
                Entity entity = findEntity(server, uuid);
                if (entity == null) continue;

                double[] last = LAST_POSITIONS.get(uuid);
                double cx = entity.getX();
                double cy = entity.getY();
                double cz = entity.getZ();

                if (last == null) {
                    LAST_POSITIONS.put(uuid, new double[]{cx, cy, cz});
                    continue;
                }

                double dx = cx - last[0];
                double dy = cy - last[1];
                double dz = cz - last[2];

                if (dx * dx + dy * dy + dz * dz < blockDelta * blockDelta) continue;

                msmp.send(notification, new NotificationPositionChangedPayload(
                    EntityResolver.toEntityRef(entity),
                    List.of(last[0], last[1], last[2]),
                    List.of(cx, cy, cz)
                ));
                LAST_POSITIONS.put(uuid, new double[]{cx, cy, cz});
            }
        });
    }

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
