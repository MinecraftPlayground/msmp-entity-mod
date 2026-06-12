package dev.loat.msmp_entity.msmp.endpoints.health.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.RPCConnectionLogger;
import dev.loat.msmp_entity.msmp.components.EntityRef;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.health.notification.changed.NotificationHealthChanged;
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
 * Registers the {@code entity:health/changed/remove} MSMP method.
 *
 * <p>Removes entities from the health change notification tracker.</p>
 */
public class HealthChangedRemove {

    private HealthChangedRemove() {}

    public static void register(MSMPNamespace namespace) {
        namespace.method(
            "health/changed/remove",
            EntityTrackerRequest.SCHEMA,
            EntityTrackerResponse.SCHEMA,
            "Remove entities from the health change notification tracker",
            (server, params, client) -> {
                if (params.entities().isEmpty()) {
                    return new EntityTrackerResponse(List.of());
                }

                EntityTracker entityTracker = EntityTracker.get(NotificationHealthChanged.TRACKER_KEY);
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

                entityTracker.remove(uuids);
                RPCConnectionLogger.info(client.connectionId(),
                    "entity:health/changed/remove - removed %s".formatted(uuids));
                return new EntityTrackerResponse(resolved);
            }
        );
    }
}
