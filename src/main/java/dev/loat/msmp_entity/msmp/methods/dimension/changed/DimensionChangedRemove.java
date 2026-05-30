package dev.loat.msmp_entity.msmp.methods.dimension.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.RPCConnectionLogger;
import dev.loat.msmp_entity.msmp.components.EntityRef;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.subscription.SubscribeRequest;
import dev.loat.msmp_entity.msmp.subscription.SubscribeResponse;
import dev.loat.msmp_entity.msmp.subscription.SubscriptionManager;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DimensionChangedRemove {

    public static void register(MSMPNamespace namespace) {
        namespace.method("dimension/changed/remove",
            SubscribeRequest.SCHEMA,
            SubscribeResponse.SCHEMA,
            "Remove entities from the dimension change notification list",
            (server, params, client) -> {
                if (params.entities().isEmpty()) {
                    return new SubscribeResponse(List.of());
                }

                SubscriptionManager manager = SubscriptionManager.get("entity:dimension/subscribe");
                Set<UUID> uuids = new HashSet<>();
                List<EntityRef> resolved = new ArrayList<>();

                for (EntityRequest entry : params.entities()) {
                    try {
                        Entity entity = EntityResolver.resolveEntity(server, entry);
                        uuids.add(entity.getUUID());
                        resolved.add(EntityResolver.toEntityRef(entity));
                    } catch (IllegalArgumentException e) {
                        RPCConnectionLogger.warning(client.connectionId(), "entity:dimension/unsubscribe - " + e.getMessage());
                        throw e;
                    }
                }

                manager.unsubscribe(uuids);
                RPCConnectionLogger.info(client.connectionId(), "entity:dimension/unsubscribe - unsubscribed from %s".formatted(uuids));
                return new SubscribeResponse(resolved);
            }
        );
    }
}
