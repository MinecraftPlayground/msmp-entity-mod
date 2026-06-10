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
public class HealthChanged {

    public static final String TRACKER_KEY = "entity:notification/health/changed";

    private HealthChanged() {}

    public static void register(MSMPNamespace namespace, Supplier<MSMPServer> msmpServer) {
        HealthChangedDispatcher.init(
            namespace.notification(
                "health/changed",
                HealthChangedPayload.SCHEMA,
                "Fired when a tracked LivingEntity's health changes"
            ),
            msmpServer
        );
    }
}
