package dev.loat.msmp_entity_data.msmp.notifications.dimension.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity_data.msmp.components.EntityResolver;
import dev.loat.msmp_entity_data.msmp.subscription.SubscriptionManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;

public class DimensionChanged {

    public static void register(
        MSMPNamespace namespace,
        MSMPServerSupplier msmpServer,
        SubscriptionManager subscriptionManager
    ) {
        MSMPNotification<DimensionChangedPayload> notification =
            namespace.notification("notification/dimension/changed", DimensionChangedPayload.SCHEMA,
                "Fired when an entity changes dimension");

        ServerEntityLevelChangeEvents.AFTER_ENTITY_CHANGE_LEVEL.register(
            (originalEntity, newEntity, origin, destination) -> {
                MSMPServer server = msmpServer.get();
                if (server == null) return;

                if (!subscriptionManager.hasSubscribers(newEntity.getUUID())) return;

                String from = origin.dimension().identifier().toString();
                String to = destination.dimension().identifier().toString();
                DimensionChangedPayload payload = new DimensionChangedPayload(
                    EntityResolver.toEntityRef(newEntity),
                    from,
                    to
                );

                for (Integer connectionId : subscriptionManager.getSubscribers(newEntity.getUUID())) {
                    server.sendTo(connectionId, notification, payload);
                }
            }
        );
    }

    @FunctionalInterface
    public interface MSMPServerSupplier {
        MSMPServer get();
    }
}
