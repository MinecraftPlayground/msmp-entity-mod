package dev.loat.msmp_entity.msmp.endpoints.health.notification.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPServer;

import java.util.function.Supplier;


/**
 * Registers the {@code entity:notification/health/changed} MSMP notification.
 *
 * <p>Uses a Mixin on {@code LivingEntity#setHealth(float)} to fire immediately
 * on every health change — no tick polling, no delta threshold, no cooldown needed.</p>
 *
 * <p>Example notification payload:</p>
 * <pre><code>
 * {
 *   "entity":     { "id": "069a...", "name": "Steve" },
 *   "from":       20.0,
 *   "to":         15.0,
 *   "max_health": 20.0
 * }
 * </code></pre>
 */
public class HealthChanged {

    /** Subscription key used with the global SubscriptionManager. */
    public static final String SUBSCRIPTION_KEY = "entity:notification/health/changed";

    private HealthChanged() {}

    /**
     * Registers the {@code entity:notification/health/changed} notification and
     * initializes the {@link HealthChangedDispatcher} so the Mixin can forward
     * events to it.
     *
     * @param namespace  The namespace to register this notification under
     * @param msmpServer Supplier for the running {@link MSMPServer}
     */
    public static void register(MSMPNamespace namespace, Supplier<MSMPServer> msmpServer) {
        HealthChangedDispatcher.init(
            namespace.notification(
                "health/changed",
                HealthChangedPayload.SCHEMA,
                "Fired when a subscribed LivingEntity's health changes"
            ),
            msmpServer
        );
    }
}
