package dev.loat.msmp_entity.msmp.endpoints.items.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.RPCConnectionLogger;
import dev.loat.msmp_entity.msmp.components.EntityRef;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.items.ItemsSnapshot;
import dev.loat.msmp_entity.msmp.endpoints.items.notification.changed.NotificationItemsChanged;
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
 * Registers the {@code entity:items/changed/add} MSMP method.
 *
 * <p>Adds {@link Player} instances to the items change notification tracker.
 * Non-player entities are rejected immediately, since only players have an inventory.
 * A fresh baseline snapshot is captured synchronously for each added player, so the
 * very next real inventory change is guaranteed to be detected and reported.</p>
 */
public class ItemsChangedAdd {

    private ItemsChangedAdd() {}

    public static void register(MSMPNamespace namespace) {

        namespace.method("items/changed/add")
            .description("Add players to the items change notification tracker")
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

                        if (!(entity instanceof Player player)) {
                            throw new IllegalArgumentException(
                                "Entity %s is not a Player and has no inventory".formatted(entity.getUUID())
                            );
                        }

                        NotificationItemsChanged.LAST_ITEMS.put(player.getUUID(), ItemsSnapshot.of(server, player));
                        uuids.add(player.getUUID());
                        resolved.add(EntityResolver.toEntityRef(player));
                    } catch (IllegalArgumentException e) {
                        RPCConnectionLogger.warning(client.connectionId(), "entity:items/changed/add - " + e.getMessage());
                        throw e;
                    }
                }

                entityTracker.add(uuids);
                RPCConnectionLogger.info(client.connectionId(),
                    "entity:items/changed/add - added %s".formatted(uuids));
                return new EntityTrackerResponse(resolved);
            });
    }
}
