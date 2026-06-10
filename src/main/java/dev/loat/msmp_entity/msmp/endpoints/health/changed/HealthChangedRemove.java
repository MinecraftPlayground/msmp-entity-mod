package dev.loat.msmp_entity.msmp.endpoints.health.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.RPCConnectionLogger;
import dev.loat.msmp_entity.msmp.components.EntityRef;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.health.notification.changed.HealthChanged;
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
 * Registers the {@code entity:health/changed/remove} MSMP subscription method.
 *
 * <p>Removes specified entities from the health change notification subscription list.</p>
 *
 * <p>Example request:</p>
 * <pre><code>
 * {
 *   "jsonrpc": "2.0", "id": 1, "method": "entity:health/changed/remove",
 *   "params": [{ "entities": [{ "name": "Steve" }] }]
 * }
 * </code></pre>
 *
 * <p>Example response:</p>
 * <pre><code>
 * { "subscribed": [{ "id": "069a...", "name": "Steve" }] }
 * </code></pre>
 */
public class HealthChangedRemove {

    private HealthChangedRemove() {}

    /**
     * Registers the {@code entity:health/changed/remove} method on the given {@link MSMPNamespace}.
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method(
            "health/changed/remove",
            SubscribeRequest.SCHEMA,
            SubscribeResponse.SCHEMA,
            "Remove entities from the health change notification list",
            (server, params, client) -> {
                if (params.entities().isEmpty()) {
                    return new SubscribeResponse(List.of());
                }

                SubscriptionManager manager = SubscriptionManager.get(HealthChanged.SUBSCRIPTION_KEY);
                Set<UUID> uuids = new HashSet<>();
                List<EntityRef> resolved = new ArrayList<>();

                for (EntityRequest entry : params.entities()) {
                    try {
                        Entity entity = EntityResolver.resolveEntity(server, entry);
                        uuids.add(entity.getUUID());
                        resolved.add(EntityResolver.toEntityRef(entity));
                    } catch (IllegalArgumentException e) {
                        RPCConnectionLogger.warning(client.connectionId(), "entity:health/changed/remove - " + e.getMessage());
                        throw e;
                    }
                }

                manager.unsubscribe(uuids);
                RPCConnectionLogger.info(
                    client.connectionId(),
                    "entity:health/changed/remove - removed %s from the health change notification list".formatted(uuids)
                );
                return new SubscribeResponse(resolved);
            }
        );
    }
}
