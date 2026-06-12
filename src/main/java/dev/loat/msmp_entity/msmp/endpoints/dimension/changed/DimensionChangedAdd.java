package dev.loat.msmp_entity.msmp.endpoints.dimension.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.RPCConnectionLogger;
import dev.loat.msmp_entity.msmp.components.EntityRef;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.dimension.notification.changed.NotificationDimensionChanged;
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
 * Registers the {@code entity:dimension/changed/add} MSMP method.
 *
 * <p>Adds entities to the dimension change notification tracker.</p>
 */
public class DimensionChangedAdd {

    private DimensionChangedAdd() {}

    public static void register(MSMPNamespace namespace) {
        namespace.method(
            "dimension/changed/add",
            EntityTrackerRequest.SCHEMA,
            EntityTrackerResponse.SCHEMA,
            "Add entities to the dimension change notification tracker",
            (server, params, client) -> {
                if (params.entities().isEmpty()) {
                    return new EntityTrackerResponse(List.of());
                }

                EntityTracker entityTracker = EntityTracker.get(NotificationDimensionChanged.TRACKER_KEY);
                Set<UUID> uuids = new HashSet<>();
                List<EntityRef> resolved = new ArrayList<>();

                for (EntityRequest entry : params.entities()) {
                    try {
                        Entity entity = EntityResolver.resolveEntity(server, entry);
                        uuids.add(entity.getUUID());
                        resolved.add(EntityResolver.toEntityRef(entity));
                    } catch (IllegalArgumentException e) {
                        RPCConnectionLogger.warning(client.connectionId(), "entity:dimension/changed/add - " + e.getMessage());
                        throw e;
                    }
                }

                entityTracker.add(uuids);
                RPCConnectionLogger.info(client.connectionId(),
                    "entity:dimension/changed/add - added %s".formatted(uuids));
                return new EntityTrackerResponse(resolved);
            }
        );
    }
}
