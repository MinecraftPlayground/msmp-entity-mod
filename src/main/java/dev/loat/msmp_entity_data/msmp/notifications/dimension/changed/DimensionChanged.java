package dev.loat.msmp_entity_data.msmp.notifications.dimension.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity_data.msmp.components.EntityResolver;
import dev.loat.msmp_entity_data.msmp.subscription.SubscriptionManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * Handles the {@code entity_data:notification/dimension/changed} notification.
 *
 * <p>Fires when any entity changes dimension. Only sent to connections that
 * have subscribed via {@code entity_data:notification/dimension/changed/subscribe}.</p>
 *
 * <p>Example notification payload:</p>
 * <pre>{@code
 * {
 *   "jsonrpc": "2.0",
 *   "method":  "entity_data:notification/dimension/changed",
 *   "params":  [{
 *     "entity": { "id": "069a...", "name": "Steve" },
 *     "from":   "minecraft:overworld",
 *     "to":     "minecraft:the_nether"
 *   }]
 * }
 * }</pre>
 */
public class DimensionChanged {

    /**
     * Registers the notification, subscribe and unsubscribe methods, and
     * hooks into the Fabric {@link ServerEntityWorldChangeEvents#AFTER_ENTITY_CHANGE_WORLD} event.
     *
     * @param namespace The namespace to register under
     * @param msmpServer Supplier of the current {@link MSMPServer} instance
     * @param subscriptionManager The {@link SubscriptionManager} for this notification
     */
    public static void register(
        MSMPNamespace namespace,
        MSMPServerSupplier msmpServer,
        SubscriptionManager subscriptionManager
    ) {
        MSMPNotification<DimensionChangedPayload> notification =
            namespace.notification("notification/dimension/changed", DimensionChangedPayload.SCHEMA,
                "Fired when an entity changes dimension");

        DimensionChangedSubscribe.register(namespace, subscriptionManager);
        DimensionChangedUnsubscribe.register(namespace, subscriptionManager);

        ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register(
            (entity, origin, destination) -> {
                MSMPServer server = msmpServer.get();
                if (server == null) return;

                if (!subscriptionManager.hasSubscribers(entity.getUUID())) return;

                String from = origin.dimension().identifier().toString();
                String to = destination.dimension().identifier().toString();
                DimensionChangedPayload payload = new DimensionChangedPayload(
                    EntityResolver.toEntityRef(entity),
                    from,
                    to
                );

                for (String connectionId : subscriptionManager.getSubscribers(entity.getUUID())) {
                    server.sendTo(connectionId, notification, payload);
                }
            }
        );
    }

    /**
     * Functional interface for lazily resolving the current {@link MSMPServer} instance.
     * Returns {@code null} when no server is running.
     */
    @FunctionalInterface
    public interface MSMPServerSupplier {
        MSMPServer get();
    }
}
