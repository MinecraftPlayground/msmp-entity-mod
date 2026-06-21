package dev.loat.msmp_entity.msmp.endpoints.saturation.notification.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.mixin_registry.FoodDataTickMixinRegistry;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import net.minecraft.world.food.FoodData;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;


/**
 * Registers the {@code entity:notification/saturation/changed} MSMP notification.
 *
 * <p>Detection is event-driven via {@link FoodDataTickMixinRegistry}, fed by a mixin on
 * {@code FoodData#tick(Player)}, which Vanilla calls exactly once per player per server
 * tick regardless of whether anything actually changed. The callback cheaply filters by
 * tracking membership, then compares the player's current food level/saturation against
 * the last recorded snapshot and fires immediately if either differs. No separate polling
 * loop or interval configuration is needed since the mixin already runs at tick
 * granularity.</p>
 */
public class NotificationSaturationChanged {

    public static final String TRACKER_KEY = "entity:notification/saturation/changed";

    /**
     * Snapshot of a player's food level and saturation at a point in time.
     */
    public record SaturationSnapshot(int food, float saturation) {}

    /**
     * Last recorded snapshot per player UUID.
     * Reset on add (fresh baseline established on the very next tick); cleared on remove.
     */
    public static final Map<UUID, SaturationSnapshot> LAST_SATURATION = new ConcurrentHashMap<>();

    private NotificationSaturationChanged() {}

    public static void register(MSMPNamespace namespace, Supplier<MSMPServer> msmpServer) {

        MSMPNotification<NotificationSaturationChangedPayload> notification = namespace.notification("saturation/changed")
            .description("Fired when a tracked player's food level or saturation changes")
            .responseSchema(NotificationSaturationChangedPayload.SCHEMA)
            .register();

        EntityTracker tracker = EntityTracker.get(TRACKER_KEY);

        FoodDataTickMixinRegistry.register(player -> {
            if (!tracker.contains(player.getUUID())) return;

            MSMPServer msmp = msmpServer.get();
            if (msmp == null) return;

            FoodData foodData = player.getFoodData();
            SaturationSnapshot current = new SaturationSnapshot(
                foodData.getFoodLevel(),
                foodData.getSaturationLevel()
            );

            UUID uuid = player.getUUID();
            SaturationSnapshot last = LAST_SATURATION.get(uuid);

            if (last == null) {
                LAST_SATURATION.put(uuid, current);
                return;
            }

            if (last.food() == current.food() && last.saturation() == current.saturation()) return;

            msmp.send(notification, new NotificationSaturationChangedPayload(
                EntityResolver.toEntityRef(player),
                last.food(),
                current.food(),
                last.saturation(),
                current.saturation()
            ));
            LAST_SATURATION.put(uuid, current);
        });
    }
}
