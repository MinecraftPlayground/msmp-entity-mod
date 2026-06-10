package dev.loat.msmp_entity.msmp.endpoints.health.notification.changed;

import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Supplier;


/**
 * Static dispatcher called by {@link dev.loat.msmp_entity.mixin.LivingEntitySetHealthMixin}
 * whenever {@code LivingEntity#setHealth(float)} is invoked.
 *
 * <p>Decouples the Mixin from the notification infrastructure.
 * Initialized once in {@link HealthChanged#register}.</p>
 */
public final class HealthChangedDispatcher {

    private static MSMPNotification<HealthChangedPayload> notification;
    private static Supplier<MSMPServer> msmpServer;

    private HealthChangedDispatcher() {}

    static void init(
        MSMPNotification<HealthChangedPayload> notification,
        Supplier<MSMPServer> msmpServer
    ) {
        HealthChangedDispatcher.notification = notification;
        HealthChangedDispatcher.msmpServer = msmpServer;
    }

    /**
     * Dispatches a health change notification if the entity is tracked and health actually changed.
     *
     * <p>Called at HEAD of {@code setHealth(float)}, so {@code oldHealth} is read
     * before the new value is applied.</p>
     */
    public static void dispatch(LivingEntity entity, float oldHealth, float newHealth) {
        if (notification == null || msmpServer == null) return;
        if (oldHealth == newHealth) return;

        if (!EntityTracker.get(HealthChanged.TRACKER_KEY).contains(entity.getUUID())) return;

        MSMPServer server = msmpServer.get();
        if (server == null) return;

        server.send(notification, new HealthChangedPayload(
            EntityResolver.toEntityRef(entity),
            oldHealth,
            newHealth,
            entity.getAttributeValue(Attributes.MAX_HEALTH)
        ));
    }
}
