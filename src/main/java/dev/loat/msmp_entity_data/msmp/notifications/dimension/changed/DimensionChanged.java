package dev.loat.msmp_entity_data.msmp.notifications.dimension.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity_data.msmp.components.EntityResolver;
import dev.loat.msmp_entity_data.msmp.subscription.SubscriptionManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public class DimensionChanged {

    public static void register(
        MSMPNamespace namespace,
        MSMPServerSupplier msmpServer
    ) {
        MSMPNotification<DimensionChangedPayload> notification =
            namespace.notification("dimension/changed", DimensionChangedPayload.SCHEMA,
                "Fired when an entity changes dimension");

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
        MSMPServerSupplier msmpServer,
        MSMPNotification<DimensionChangedPayload> notification,
        Entity entity,
        ServerLevel origin,
        ServerLevel destination
    ) {
        MSMPServer server = msmpServer.get();
        if (server == null) return;

        SubscriptionManager manager = SubscriptionManager.get("entity_data:dimension/subscribe");
        if (!manager.isSubscribed(entity.getUUID())) return;

        DimensionChangedPayload payload = new DimensionChangedPayload(
            EntityResolver.toEntityRef(entity),
            origin.dimension().identifier().toString(),
            destination.dimension().identifier().toString()
        );

        server.send(notification, payload);
    }

    @FunctionalInterface
    public interface MSMPServerSupplier {
        MSMPServer get();
    }
}
