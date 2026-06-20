package dev.loat.msmp_entity.msmp.endpoints.rotation.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.RPCConnectionLogger;
import dev.loat.msmp_entity.msmp.components.EntityRef;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.rotation.notification.changed.NotificationRotationChanged;
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
 * Registers the {@code entity:rotation/changed/add} MSMP method.
 *
 * <p>Adds entities to the rotation change notification tracker.
 * The last-rotation baseline is reset on add to avoid stale notifications.</p>
 */
public class RotationChangedAdd {

    private RotationChangedAdd() {}

    public static void register(MSMPNamespace namespace) {

        namespace.method("rotation/changed/add")
            .description("Add entities to the rotation change notification tracker")
            .requestSchema(EntityTrackerRequest.SCHEMA)
            .responseSchema(EntityTrackerResponse.SCHEMA)
            .register((server, client, params) -> {
                if (params.entities().isEmpty()) {
                    return new EntityTrackerResponse(List.of());
                }

                EntityTracker entityTracker = EntityTracker.get(NotificationRotationChanged.TRACKER_KEY);
                Set<UUID> uuids = new HashSet<>();
                List<EntityRef> resolved = new ArrayList<>();

                for (EntityRequest entry : params.entities()) {
                    try {
                        Entity entity = EntityResolver.resolveEntity(server, entry);
                        NotificationRotationChanged.LAST_ROTATIONS.remove(entity.getUUID());
                        uuids.add(entity.getUUID());
                        resolved.add(EntityResolver.toEntityRef(entity));
                    } catch (IllegalArgumentException e) {
                        RPCConnectionLogger.warning(client.connectionId(), "entity:rotation/changed/add - " + e.getMessage());
                        throw e;
                    }
                }

                entityTracker.add(uuids);
                RPCConnectionLogger.info(client.connectionId(),
                    "entity:rotation/changed/add - added %s".formatted(uuids));
                return new EntityTrackerResponse(resolved);
            });
    }
}
