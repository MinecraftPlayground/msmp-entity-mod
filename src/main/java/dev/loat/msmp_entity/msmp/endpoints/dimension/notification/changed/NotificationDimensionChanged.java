package dev.loat.msmp_entity.msmp.endpoints.dimension.notification.changed;

import java.util.function.Supplier;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;


/**
 * Registers the {@code entity:notification/dimension/changed} MSMP notification.
 *
 * <p>Fires when a tracked entity changes dimension.</p>
 */
public class NotificationDimensionChanged {

    public static final String TRACKER_KEY = "entity:notification/dimension/changed";

    private NotificationDimensionChanged() {}

    public static void register(MSMPNamespace namespace, Supplier<MSMPServer> msmpServer) {
        MSMPNotification<NotificationDimensionChangedPayload> notification =
            namespace.notification("dimension/changed")
                .description("Fires when a tracked entity changes dimension")
                .responseSchema(NotificationDimensionChangedPayload.SCHEMA)
                .register();

        ServerEntityLevelChangeEvents.AFTER_ENTITY_CHANGE_LEVEL.register(
            (originalEntity, newEntity, origin, destination) ->
                dispatch(msmpServer, notification, newEntity, origin, destination)
        );

        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register(
            (player, origin, destination) ->
                dispatch(msmpServer, notification, player, origin, destination)
        );
    }

    private static void dispatch(
        Supplier<MSMPServer> msmpServer,
        MSMPNotification<NotificationDimensionChangedPayload> notification,
        Entity entity,
        ServerLevel origin,
        ServerLevel destination
    ) {
        MSMPServer server = msmpServer.get();
        if (server == null) return;

        if (!EntityTracker.get(TRACKER_KEY).contains(entity.getUUID())) return;

        server.send(notification, new NotificationDimensionChangedPayload(
            EntityResolver.toEntityRef(entity),
            origin.dimension().identifier().toString(),
            destination.dimension().identifier().toString()
        ));
    }
}
