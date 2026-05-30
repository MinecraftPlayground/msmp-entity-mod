package dev.loat.msmp_entity.msmp.endpoints.dimension.changed;

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

/**
 * Registers the {@code entity:dimension/changed/remove} MSMP subscription method.
 *
 * <p>Removes specified entities from the dimension change notification subscription list.
 * When a subscribed entity changes dimensions, clients receive notifications. This method
 * allows clients to unsubscribe from receiving further notifications for the given entities.</p>
 *
 * <p>Example request:</p>
 * <pre><code>
 * {
 *   "jsonrpc": "2.0",
 *   "id": 1,
 *   "method": "entity:dimension/changed/remove",
 *   "params": [{ "entities": [{ "uuid": "069a79f4-44e9-4726-a5be-fca90e38aaf5" }] }]
 * }
 * </code></pre>
 *
 * <p>Example response:</p>
 * <pre><code>
 * { "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5", "name": "Steve" }
 * </code></pre>
 */
public class DimensionChangedRemove {

    private DimensionChangedRemove() {}

    /**
     * Registers the {@code entity:dimension/changed/remove} method on the given {@link MSMPNamespace}.
     *
     * <p>Resolves the provided entities and removes them from the dimension change notification
     * subscription list. If the entity list is empty, returns an empty response immediately.
     * Throws {@link IllegalArgumentException} if any entity cannot be resolved.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method(
            "dimension/changed/remove",
            SubscribeRequest.SCHEMA,
            SubscribeResponse.SCHEMA,
            "Remove entities from the dimension change notification list",
            (server, params, client) -> {
                if (params.entities().isEmpty()) {
                    return new SubscribeResponse(List.of());
                }

                SubscriptionManager manager = SubscriptionManager.get("entity:notification/dimension/changed");
                Set<UUID> uuids = new HashSet<>();
                List<EntityRef> resolved = new ArrayList<>();

                for (EntityRequest entry : params.entities()) {
                    try {
                        Entity entity = EntityResolver.resolveEntity(server, entry);
                        uuids.add(entity.getUUID());
                        resolved.add(EntityResolver.toEntityRef(entity));
                    } catch (IllegalArgumentException e) {
                        RPCConnectionLogger.warning(client.connectionId(), "entity:dimension/changed/remove - " + e.getMessage());
                        throw e;
                    }
                }

                manager.unsubscribe(uuids);
                RPCConnectionLogger.info(
                    client.connectionId(),
                    "entity:dimension/changed/remove - Removed %s from the dimension change notification list".formatted(uuids)
                );
                return new SubscribeResponse(resolved);
            }
        );
    }
}
