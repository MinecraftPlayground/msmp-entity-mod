package dev.loat.msmp_entity.msmp.endpoints.health.notification.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.mixin_registry.LivingEntitySetHealthMixinRegistry;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Supplier;


/**
 * Registers the {@code entity:notification/health/changed} MSMP notification.
 *
 * <p>Uses a Mixin on {@code LivingEntity#setHealth(float)} to fire immediately
 * on every health change of a tracked entity.</p>
 */
public class NotificationHealthChanged {

    public static final String TRACKER_KEY = "entity:notification/health/changed";

    private NotificationHealthChanged() {}

    public static void register(MSMPNamespace namespace, Supplier<MSMPServer> msmpServer) {
        MSMPNotification<NotificationHealthChangedPayload> notification = namespace.notification("health/changed")
            .description("Fired when a tracked LivingEntity's health changes")
            .responseSchema(NotificationHealthChangedPayload.SCHEMA)
            .register();

        LivingEntitySetHealthMixinRegistry.register((entity, oldHealth, newHealth) -> {
            if (notification == null || msmpServer == null) return;
            if (oldHealth == newHealth) return;

            if (!EntityTracker.get(NotificationHealthChanged.TRACKER_KEY).contains(entity.getUUID())) return;

            MSMPServer server = msmpServer.get();
            if (server == null) return;

            server.send(notification, new NotificationHealthChangedPayload(
                EntityResolver.toEntityRef(entity),
                oldHealth,
                newHealth,
                entity.getAttributeValue(Attributes.MAX_HEALTH)
            ));
        });
    }
}
