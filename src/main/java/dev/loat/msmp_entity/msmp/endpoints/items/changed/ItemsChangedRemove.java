package dev.loat.msmp_entity.msmp.endpoints.items.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.RPCConnectionLogger;
import dev.loat.msmp_entity.msmp.components.EntityRef;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.items.notification.changed.NotificationItemsChanged;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTrackerRequest;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTrackerResponse;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Registers the {@code entity:items/changed/remove} MSMP method.
 *
 * <p>Removes entities from the items change notification tracker
 * and clears their cached snapshot and any pending dirty state.</p>
 */
public class ItemsChangedRemove {

    private ItemsChangedRemove() {}

    public static void register(MSMPNamespace namespace) {

        namespace.method("items/changed/remove")
            .description("Remove entities from the items change notification tracker")
            .requestSchema(EntityTrackerRequest.SCHEMA)
            .responseSchema(EntityTrackerResponse.SCHEMA)
            .register((server, client, params) -> {
                if (params.entities().isEmpty()) {
                    return new EntityTrackerResponse(List.of());
                }

                EntityTracker entityTracker = EntityTracker.get(NotificationItemsChanged.TRACKER_KEY);
                Set<UUID> uuids = new HashSet<>();
                List<EntityRef> resolved = new ArrayList<>();

                for (EntityRequest entry : params.entities()) {
                    try {
                        Entity entity = EntityResolver.resolveEntity(server, entry);
                        uuids.add(entity.getUUID());
                        resolved.add(EntityResolver.toEntityRef(entity));
                    } catch (IllegalArgumentException e) {
                        RPCConnectionLogger.warning(client.connectionId(), "entity:items/changed/remove - " + e.getMessage());
                        throw e;
                    }
                }

                entityTracker.remove(uuids);
                uuids.forEach(NotificationItemsChanged::forget);

                RPCConnectionLogger.info(client.connectionId(),
                    "entity:items/changed/remove - removed %s".formatted(uuids));
                return new EntityTrackerResponse(resolved);
            }
        );
    }
}
