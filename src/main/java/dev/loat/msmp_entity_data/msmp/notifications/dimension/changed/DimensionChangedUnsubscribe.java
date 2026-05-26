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

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Registers the {@code entity_data:notification/dimension/changed/unsubscribe} MSMP method.
 *
 * <p>Example requests:</p>
 * <pre>{@code
 * // Unsubscribe from all:
 * { "jsonrpc": "2.0", "id": 1,
 *   "method": "entity_data:notification/dimension/changed/unsubscribe",
 *   "params": [{}] }
 *
 * // Unsubscribe from specific entity:
 * { "jsonrpc": "2.0", "id": 1,
 *   "method": "entity_data:notification/dimension/changed/unsubscribe",
 *   "params": [{ "name": "Steve" }] }
 * }</pre>
 */
public class DimensionChangedUnsubscribe {

    /**
     * Registers the {@code entity_data:notification/dimension/changed/unsubscribe} method.
     *
     * @param namespace           The namespace to register this method under
     * @param subscriptionManager The {@link SubscriptionManager} for this notification
     */
    public static void register(MSMPNamespace namespace, SubscriptionManager subscriptionManager) {
        namespace.method("notification/dimension/changed/unsubscribe",
            SubscribeRequest.SCHEMA,
            SubscribeResponse.SCHEMA,
            "Unsubscribe from dimension change notifications",
            (server, params, client) -> {
                if (params.isWildcard()) {
                    subscriptionManager.removeAll(client.connectionId().toString());
                    Logger.info("entity_data:notification/dimension/changed/unsubscribe - connection %s unsubscribed from all".formatted(client.connectionId()));
                    return new SubscribeResponse(List.of());
                }

                try {
                    Entity entity = EntityResolver.resolveEntity(server, params);
                    EntityRef ref = EntityResolver.toEntityRef(entity);
                    subscriptionManager.unsubscribe(client.connectionId().toString(), Set.of(entity.getUUID()));
                    Logger.info("entity_data:notification/dimension/changed/unsubscribe - connection %s unsubscribed from %s".formatted(client.connectionId(), entity.getUUID()));
                    return new SubscribeResponse(List.of(ref));
                } catch (MSMPException e) {
                    Logger.warning("entity_data:notification/dimension/changed/unsubscribe - " + e.getMessage());
                    throw e;
                }
            }
        );
    }
}
