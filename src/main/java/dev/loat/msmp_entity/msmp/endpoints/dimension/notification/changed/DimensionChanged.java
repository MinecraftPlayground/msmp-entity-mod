package dev.loat.msmp_entity.msmp.endpoints.dimension.notification.changed;

import java.util.function.Supplier;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.subscription.SubscriptionManager;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;


/**
 * Registers the {@code entity:dimension/changed} MSMP notification method.
 *
 * <p>Fires when an entity changes dimension. Clients can subscribe to this notification to receive
 * updates whenever a specified entity changes dimensions.</p>
 *
 * <p>Example notification payload:</p>
 * <pre><code>
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "origin": "minecraft:overworld",
 *   "destination": "minecraft:the_nether"
 * }
 * </code></pre>
 */
public class DimensionChanged {

    private DimensionChanged() {}

    /**
     * Registers the {@code entity:dimension/changed} notification method and its associated event listeners.
     *
     * <p>Listens for entity dimension change events and dispatches notifications to subscribed clients.
     * The notification is only sent if the entity that changed dimensions is currently subscribed to
     * receive dimension change notifications.</p>
     *
     * @param namespace The namespace to register this notification under
     * @param msmpServer A supplier that provides access to the MSMPServer instance for sending notifications
     */
    public static void register(
        MSMPNamespace namespace,
        Supplier<MSMPServer> msmpServer
    ) {
        MSMPNotification<DimensionChangedPayload> notification =
            namespace.notification(
                "dimension/changed",
                DimensionChangedPayload.SCHEMA,
                "Fires when an entity changes dimension"
            );

        ServerEntityLevelChangeEvents.AFTER_ENTITY_CHANGE_LEVEL.register(
            (originalEntity, newEntity, origin, destination) ->
                dispatch(msmpServer, notification, newEntity, origin, destination)
        );

        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register(
            (player, origin, destination) ->
                dispatch(msmpServer, notification, player, origin, destination)
        );
    }

    /**
     * Dispatches the dimension change notification to subscribed clients.
     *
     * @param msmpServer A supplier that provides access to the MSMPServer instance for sending notifications
     * @param notification The notification to dispatch
     * @param entity The entity that changed dimensions
     * @param origin The origin dimension
     * @param destination The destination dimension
     */
    private static void dispatch(
        Supplier<MSMPServer> msmpServer,
        MSMPNotification<DimensionChangedPayload> notification,
        Entity entity,
        ServerLevel origin,
        ServerLevel destination
    ) {
        MSMPServer server = msmpServer.get();
        if (server == null) return;

        SubscriptionManager manager = SubscriptionManager.get("entity:notification/dimension/changed");
        if (!manager.isSubscribed(entity.getUUID())) return;

        DimensionChangedPayload payload = new DimensionChangedPayload(
            EntityResolver.toEntityRef(entity),
            origin.dimension().identifier().toString(),
            destination.dimension().identifier().toString()
        );

        server.send(notification, payload);
    }
}
