package dev.loat.msmp_entity.msmp.endpoints.health.notification.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPServer;

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
        NotificationHealthChangedDispatcher.init(
            namespace.notification("health/changed")
                .description("Fired when a tracked LivingEntity's health changes")
                .responseSchema(NotificationHealthChangedPayload.SCHEMA)
                .register(),
            msmpServer
        );
    }
}
