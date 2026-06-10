package dev.loat.msmp_entity.msmp.endpoints.health.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.RPCConnectionLogger;
import dev.loat.msmp_entity.msmp.components.EntityRef;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.health.notification.changed.HealthChanged;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTrackerRequest;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTrackerResponse;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Registers the {@code entity:health/changed/add} MSMP method.
 *
 * <p>Adds {@link LivingEntity} instances to the health change notification tracker.
 * Non-living entities are rejected immediately.</p>
 */
public class HealthChangedAdd {

    private HealthChangedAdd() {}

    public static void register(MSMPNamespace namespace) {
        namespace.method(
            "health/changed/add",
            EntityTrackerRequest.SCHEMA,
            EntityTrackerResponse.SCHEMA,
            "Add LivingEntities to the health change notification tracker",
            (server, params, client) -> {
                if (params.entities().isEmpty()) {
                    return new EntityTrackerResponse(List.of());
                }

                EntityTracker entityTracker = EntityTracker.get(HealthChanged.TRACKER_KEY);
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

                        uuids.add(entity.getUUID());
                        resolved.add(EntityResolver.toEntityRef(entity));
                    } catch (IllegalArgumentException e) {
                        RPCConnectionLogger.warning(client.connectionId(), "entity:health/changed/add - " + e.getMessage());
                        throw e;
                    }
                }

                entityTracker.add(uuids);
                RPCConnectionLogger.info(client.connectionId(),
                    "entity:health/changed/add - added %s".formatted(uuids));
                return new EntityTrackerResponse(resolved);
            }
        );
    }
}
