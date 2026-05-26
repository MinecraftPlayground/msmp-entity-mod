package dev.loat.msmp_entity_data.msmp.notifications.dimension.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.logging.Logger;
import dev.loat.msmp_entity_data.msmp.components.EntityRef;
import dev.loat.msmp_entity_data.msmp.components.EntityResolver;
import dev.loat.msmp_entity_data.msmp.exceptions.MSMPException;
import dev.loat.msmp_entity_data.msmp.subscription.SubscribeRequest;
import dev.loat.msmp_entity_data.msmp.subscription.SubscribeResponse;
import dev.loat.msmp_entity_data.msmp.subscription.SubscriptionManager;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Registers the {@code entity_data:notification/dimension/changed/subscribe} MSMP method.
 *
 * <p>Example requests:</p>
 * <pre>{@code
 * // Wildcard — all entities:
 * { "jsonrpc": "2.0", "id": 1,
 *   "method": "entity_data:notification/dimension/changed/subscribe",
 *   "params": [{}] }
 *
 * // Specific player by name:
 * { "jsonrpc": "2.0", "id": 1,
 *   "method": "entity_data:notification/dimension/changed/subscribe",
 *   "params": [{ "name": "Steve" }] }
 * }</pre>
 */
public class DimensionChangedSubscribe {

    /**
     * Registers the {@code entity_data:notification/dimension/changed/subscribe} method.
     *
     * @param namespace           The namespace to register this method under
     * @param subscriptionManager The {@link SubscriptionManager} for this notification
     */
    public static void register(MSMPNamespace namespace, SubscriptionManager subscriptionManager) {
        namespace.method("notification/dimension/changed/subscribe",
            SubscribeRequest.SCHEMA,
            SubscribeResponse.SCHEMA,
            "Subscribe to dimension change notifications for a specific entity or all entities",
            (server, params, client) -> {
                if (params.isWildcard()) {
                    subscriptionManager.subscribe(client.connectionId().toString(), Set.of(SubscriptionManager.WILDCARD));
                    Logger.info("entity_data:notification/dimension/changed/subscribe - connection %s subscribed to all entities".formatted(client.connectionId()));
                    return new SubscribeResponse(List.of());
                }

                try {
                    Entity entity = EntityResolver.resolveEntity(server, params);
                    UUID uuid = entity.getUUID();
                    subscriptionManager.subscribe(client.connectionId().toString(), Set.of(uuid));
                    EntityRef ref = EntityResolver.toEntityRef(entity);
                    Logger.info("entity_data:notification/dimension/changed/subscribe - connection %s subscribed to %s".formatted(client.connectionId(), uuid));
                    return new SubscribeResponse(List.of(ref));
                } catch (MSMPException e) {
                    Logger.warning("entity_data:notification/dimension/changed/subscribe - " + e.getMessage());
                    throw e;
                }
            }
        );
    }
}
