package dev.loat.msmp_entity.msmp.endpoints.items.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.items.notification.changed.NotificationItemsChanged;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTrackerResponse;

/**
 * Registers the {@code entity:items/changed} MSMP method.
 *
 * <p>Returns a list with all tracked entities for the items changed event</p>
 */
public class ItemsChanged {

    private ItemsChanged() {}

    public static void register(MSMPNamespace namespace) {

        namespace.method("items/changed")
            .description("Returns a list of all tracked entities for the items changed event")
            .responseSchema(EntityTrackerResponse.SCHEMA)
            .register((server, client) -> {
                EntityTracker entityTracker = EntityTracker.get(NotificationItemsChanged.TRACKER_KEY);

                return new EntityTrackerResponse(
                    entityTracker.entities().stream()
                        .map(uuid -> EntityResolver.toEntityRef(EntityResolver.resolveEntityByUUID(server, uuid)))
                        .toList()
                );
            });
    }
}
