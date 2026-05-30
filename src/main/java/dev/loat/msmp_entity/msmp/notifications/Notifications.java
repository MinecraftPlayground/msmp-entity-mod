package dev.loat.msmp_entity.msmp.notifications;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.msmp.notifications.dimension.changed.DimensionChanged;


/**
 * Central registration point for all {@code entity} MSMP notifications.
 *
 * <p>Each notification is implemented in its own sub-package and registered here.
 * Call {@link #register(MSMPNamespace, DimensionChanged.MSMPServerSupplier)} once
 * during mod initialization, before the server starts.</p>
 *
 * <p>Registered notifications:</p>
 * <ul>
 *   <li>{@code entity:notification/dimension/changed} — Fired when an entity changes dimension</li>
 * </ul>
 */
public class Notifications {

    private Notifications() {}

    /**
     * Registers all {@code entity} notifications and their subscribe/unsubscribe
     * methods on the given {@link MSMPNamespace}.
     *
     * @param namespace  The namespace to register all notifications under
     * @param msmpServer Supplier of the current {@link MSMPServer} instance
     */
    public static void register(
        MSMPNamespace namespace,
        DimensionChanged.MSMPServerSupplier msmpServer
    ) {
        // DimensionChanged.register(namespace, msmpServer);
    }
}
