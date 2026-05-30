package dev.loat.msmp_entity.msmp.endpoints.dimension.notification.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.subscription.SubscriptionManager;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class DimensionChanged {

    public static void register(
        MSMPNamespace namespace,
        MSMPServer msmpServer
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


    private static void dispatch(
        MSMPServer msmpServer,
        MSMPNotification<DimensionChangedPayload> notification,
        Entity entity,
        ServerLevel origin,
        ServerLevel destination
    ) {
        if (msmpServer == null) return;

        SubscriptionManager manager = SubscriptionManager.get("entity:notification/dimension/changed");
        if (!manager.isSubscribed(entity.getUUID())) return;

        DimensionChangedPayload payload = new DimensionChangedPayload(
            EntityResolver.toEntityRef(entity),
            origin.dimension().identifier().toString(),
            destination.dimension().identifier().toString()
        );

        msmpServer.send(notification, payload);
    }
}
