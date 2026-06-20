package dev.loat.msmp_entity.msmp.endpoints.rotation.notification.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.config.Config;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;


/**
 * Registers the {@code entity:notification/rotation/changed} MSMP notification and its
 * tick-based polling loop.
 *
 * <p>Checks every {@code intervalTicks} ticks whether a tracked entity has rotated at
 * least {@code rotationDelta} degrees (in yaw or pitch) since its last recorded rotation.
 * Polling with a delta threshold is used instead of an immediate per-mutation hook because
 * rotation changes continuously during normal play (e.g. every tick while looking around),
 * so an instant push would flood subscribers.</p>
 */
public class NotificationRotationChanged {

    public static final String TRACKER_KEY = "entity:notification/rotation/changed";

    /**
     * Last recorded rotation ({@code [yaw, pitch]}) per entity UUID.
     * Reset on add (fresh baseline); cleared on remove (memory cleanup).
     */
    public static final Map<UUID, float[]> LAST_ROTATIONS = new ConcurrentHashMap<>();

    private NotificationRotationChanged() {}

    public static void register(MSMPNamespace namespace, Supplier<MSMPServer> msmpServer) {

        MSMPNotification<NotificationRotationChangedPayload> notification = namespace.notification("rotation/changed")
            .description("Fired when a tracked entity rotates at least rotationDelta degrees (checked every intervalTicks ticks)")
            .responseSchema(NotificationRotationChangedPayload.SCHEMA)
            .register();

        EntityTracker tracker = EntityTracker.get(TRACKER_KEY);
        int[] tickCounter = {0};

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter[0]++;

            int interval = Config.getConfig().rotation.notification.intervalTicks;
            if (interval < 1) interval = 1;

            if (tickCounter[0] % interval != 0) return;

            MSMPServer msmp = msmpServer.get();
            if (msmp == null) return;

            double rotationDelta = Config.getConfig().rotation.notification.rotationDelta;

            Set<UUID> entities = tracker.entities();
            if (entities.isEmpty()) return;

            for (UUID uuid : entities) {
                Entity entity = findEntity(server, uuid);
                if (entity == null) continue;

                float[] last = LAST_ROTATIONS.get(uuid);
                float currentYaw = entity.getYRot();
                float currentPitch = entity.getXRot();

                if (last == null) {
                    LAST_ROTATIONS.put(uuid, new float[]{currentYaw, currentPitch});
                    continue;
                }

                float yawDelta = Math.abs(Mth.wrapDegrees(currentYaw - last[0]));
                float pitchDelta = Math.abs(currentPitch - last[1]);

                if (yawDelta < rotationDelta && pitchDelta < rotationDelta) continue;

                msmp.send(notification, new NotificationRotationChangedPayload(
                    EntityResolver.toEntityRef(entity),
                    List.of((double) last[0], (double) last[1]),
                    List.of((double) currentYaw, (double) currentPitch)
                ));
                LAST_ROTATIONS.put(uuid, new float[]{currentYaw, currentPitch});
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
