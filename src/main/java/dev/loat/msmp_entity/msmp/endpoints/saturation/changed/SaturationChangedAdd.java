package dev.loat.msmp_entity.msmp.endpoints.saturation.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.RPCConnectionLogger;
import dev.loat.msmp_entity.msmp.components.EntityRef;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.saturation.notification.changed.NotificationSaturationChanged;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTrackerRequest;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTrackerResponse;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * Registers the {@code entity:saturation/changed/add} MSMP method.
 *
 * <p>Adds {@link Player} instances to the saturation change notification tracker.
 * Non-player entities are rejected immediately, since only players have food data.
 * The last-recorded baseline is reset on add to avoid stale notifications.</p>
 */
public class SaturationChangedAdd {

    private SaturationChangedAdd() {}

    public static void register(MSMPNamespace namespace) {

        namespace.method("saturation/changed/add")
            .description("Add players to the saturation change notification tracker")
            .requestSchema(EntityTrackerRequest.SCHEMA)
            .responseSchema(EntityTrackerResponse.SCHEMA)
            .register((server, client, params) -> {
                if (params.entities().isEmpty()) {
                    return new EntityTrackerResponse(List.of());
                }

                EntityTracker entityTracker = EntityTracker.get(NotificationSaturationChanged.TRACKER_KEY);
                Set<UUID> uuids = new HashSet<>();
                List<EntityRef> resolved = new ArrayList<>();

                for (EntityRequest entry : params.entities()) {
                    try {
                        Entity entity = EntityResolver.resolveEntity(server, entry);

                        if (!(entity instanceof Player)) {
                            throw new IllegalArgumentException(
                                "Entity %s is not a Player and has no food data".formatted(entity.getUUID())
                            );
                        }

                        NotificationSaturationChanged.LAST_SATURATION.remove(entity.getUUID());
                        uuids.add(entity.getUUID());
                        resolved.add(EntityResolver.toEntityRef(entity));
                    } catch (IllegalArgumentException e) {
                        RPCConnectionLogger.warning(client.connectionId(), "entity:saturation/changed/add - " + e.getMessage());
                        throw e;
                    }
                }

                entityTracker.add(uuids);
                RPCConnectionLogger.info(client.connectionId(),
                    "entity:saturation/changed/add - added %s".formatted(uuids));
                return new EntityTrackerResponse(resolved);
            });
    }
}
