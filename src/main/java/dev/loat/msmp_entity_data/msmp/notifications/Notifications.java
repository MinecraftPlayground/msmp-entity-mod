package dev.loat.msmp_entity_data.msmp.notifications;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity_data.msmp.notifications.dimension.changed.DimensionChanged;
import dev.loat.msmp_entity_data.msmp.subscription.SubscriptionManager;

/**
 * Central registration point for all {@code entity_data} MSMP notifications.
 *
 * <p>Each notification is implemented in its own sub-package and registered here.
 * Call {@link #register(MSMPNamespace, DimensionChanged.MSMPServerSupplier)} once
 * during mod initialization, before the server starts.</p>
 *
 * <p>Registered notifications:</p>
 * <ul>
 *   <li>{@code entity_data:notification/dimension/changed} — Fired when an entity changes dimension</li>
 * </ul>
 */
public class Notifications {

    private Notifications() {}

    /**
     * Registers all {@code entity_data} notifications and their subscribe/unsubscribe
     * methods on the given {@link MSMPNamespace}.
     *
     * @param namespace  The namespace to register all notifications under
     * @param msmpServer Supplier of the current {@link MSMPServer} instance
     */
    public static void register(
        MSMPNamespace namespace,
        DimensionChanged.MSMPServerSupplier msmpServer,
        SubscriptionManager dimensionSubscriptionManager
    ) {
        DimensionChanged.register(namespace, msmpServer, dimensionSubscriptionManager);
    }
}
