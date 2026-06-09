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
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Registers the {@code entity:health/changed/add} MSMP subscription method.
 *
 * <p>Adds specified entities to the health change notification subscription list.
 * Only {@link LivingEntity} instances are accepted — entities without health
 * (e.g. dropped items, boats) are rejected with an error.</p>
 *
 * <p>Example request:</p>
 * <pre><code>
 * {
 *   "jsonrpc": "2.0", "id": 1, "method": "entity:health/changed/add",
 *   "params": [{ "entities": [{ "name": "Steve" }] }]
 * }
 * </code></pre>
 *
 * <p>Example response:</p>
 * <pre><code>
 * { "subscribed": [{ "id": "069a...", "name": "Steve" }] }
 * </code></pre>
 */
public class HealthChangedAdd {

    private HealthChangedAdd() {}

    /**
     * Registers the {@code entity:health/changed/add} method on the given {@link MSMPNamespace}.
     *
     * <p>Resolves the provided entities, validates they are {@link LivingEntity} instances,
     * and adds them to the health change notification subscription list.
     * If the entity list is empty, returns an empty response immediately.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method(
            "health/changed/add",
            SubscribeRequest.SCHEMA,
            SubscribeResponse.SCHEMA,
            "Add LivingEntities to the health change notification list",
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

                        if (!(entity instanceof LivingEntity)) {
                            throw new IllegalArgumentException(
                                "Entity %s is not a LivingEntity and has no health".formatted(entity.getUUID())
                            );
                        }

                        // Reset last-known health so the first poll establishes a fresh baseline
                        // rather than firing immediately based on a stale value from a previous subscription.
                        HealthChanged.LAST_HEALTH.remove(entity.getUUID());

                        uuids.add(entity.getUUID());
                        resolved.add(EntityResolver.toEntityRef(entity));
                    } catch (IllegalArgumentException e) {
                        RPCConnectionLogger.warning(client.connectionId(), "entity:health/changed/add - " + e.getMessage());
                        throw e;
                    }
                }

                manager.subscribe(uuids);
                RPCConnectionLogger.info(
                    client.connectionId(),
                    "entity:health/changed/add - added %s to the health change notification list".formatted(uuids)
                );
                return new SubscribeResponse(resolved);
            }
        );
    }
}
