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

public class DimensionUnsubscribe {

    public static void register(MSMPNamespace namespace) {
        namespace.method("dimension/unsubscribe",
            SubscribeRequest.SCHEMA,
            SubscribeResponse.SCHEMA,
            "Unsubscribe from dimension change notifications for the given entities",
            (server, params, client) -> {
                if (params.entities().isEmpty()) {
                    return new SubscribeResponse(List.of());
                }

                SubscriptionManager manager = SubscriptionManager.get("entity_data:dimension/subscribe");
                Set<UUID> uuids = new HashSet<>();
                List<EntityRef> resolved = new ArrayList<>();

                for (EntityRequest entry : params.entities()) {
                    try {
                        Entity entity = EntityResolver.resolveEntity(server, entry);
                        uuids.add(entity.getUUID());
                        resolved.add(EntityResolver.toEntityRef(entity));
                    } catch (IllegalArgumentException e) {
                        RPCConnectionLogger.warning(client.connectionId(), "entity_data:dimension/unsubscribe - " + e.getMessage());
                        throw e;
                    }
                }

                manager.unsubscribe(uuids);
                RPCConnectionLogger.info(client.connectionId(), "entity_data:dimension/unsubscribe - unsubscribed from %s".formatted(uuids));
                return new SubscribeResponse(resolved);
            }
        );
    }
}
