package dev.loat.msmp_entity_data.msmp.methods.dimension.subscribe;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.logging.RPCConnectionLogger;
import dev.loat.msmp_entity_data.msmp.components.EntityRef;
import dev.loat.msmp_entity_data.msmp.components.EntityRequest;
import dev.loat.msmp_entity_data.msmp.components.EntityResolver;
import dev.loat.msmp_entity_data.msmp.subscription.SubscribeRequest;
import dev.loat.msmp_entity_data.msmp.subscription.SubscribeResponse;
import dev.loat.msmp_entity_data.msmp.subscription.SubscriptionManager;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DimensionSubscribe {

    public static void register(MSMPNamespace namespace, SubscriptionManager subscriptionManager) {
        namespace.method("dimension/subscribe",
            SubscribeRequest.SCHEMA,
            SubscribeResponse.SCHEMA,
            "Subscribe to dimension change notifications for the given entities",
            (server, params, client) -> {
                if (params.entities().isEmpty()) {
                    return new SubscribeResponse(List.of());
                }

                int connectionId = client.connectionId();
                Set<UUID> uuids = new HashSet<>();
                List<EntityRef> resolved = new ArrayList<>();

                for (EntityRequest entry : params.entities()) {
                    try {
                        Entity entity = EntityResolver.resolveEntity(server, entry);
                        uuids.add(entity.getUUID());
                        resolved.add(EntityResolver.toEntityRef(entity));
                    } catch (IllegalArgumentException e) {
                        RPCConnectionLogger.warning(client.connectionId(), "entity_data:dimension/subscribe - %s".formatted(e.getMessage()));
                        throw e;
                    }
                }

                subscriptionManager.subscribe(connectionId, uuids);
                RPCConnectionLogger.info(client.connectionId(), "entity_data:dimension/subscribe - subscribed to %s".formatted(uuids));
                return new SubscribeResponse(resolved);
            }
        );
    }
}
