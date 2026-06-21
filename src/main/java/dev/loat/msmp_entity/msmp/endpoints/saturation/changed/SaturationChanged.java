package dev.loat.msmp_entity.msmp.endpoints.saturation.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.saturation.notification.changed.NotificationSaturationChanged;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTrackerResponse;

/**
 * Registers the {@code entity:saturation/changed} MSMP method.
 *
 * <p>Returns a list with all tracked entities for the saturation changed event</p>
 */
public class SaturationChanged {

    private SaturationChanged() {}

    public static void register(MSMPNamespace namespace) {

        namespace.method("saturation/changed")
            .description("Returns a list of all tracked entities for the saturation changed event")
            .responseSchema(EntityTrackerResponse.SCHEMA)
            .register((server, client) -> {
                EntityTracker entityTracker = EntityTracker.get(NotificationSaturationChanged.TRACKER_KEY);

                return new EntityTrackerResponse(
                    entityTracker.entities().stream()
                        .map(uuid -> EntityResolver.toEntityRef(EntityResolver.resolveEntityByUUID(server, uuid)))
                        .toList()
                );
            });
    }
}
