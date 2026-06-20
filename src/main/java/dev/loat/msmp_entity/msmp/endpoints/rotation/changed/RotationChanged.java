package dev.loat.msmp_entity.msmp.endpoints.rotation.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.rotation.notification.changed.NotificationRotationChanged;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTrackerResponse;

/**
 * Registers the {@code entity:rotation/changed} MSMP method.
 *
 * <p>Returns a list with all tracked entities for the rotation changed event</p>
 */
public class RotationChanged {

    private RotationChanged() {}

    public static void register(MSMPNamespace namespace) {

        namespace.method("rotation/changed")
            .description("Returns a list of all tracked entities for the rotation changed event")
            .responseSchema(EntityTrackerResponse.SCHEMA)
            .register((server, client) -> {
                EntityTracker entityTracker = EntityTracker.get(NotificationRotationChanged.TRACKER_KEY);

                return new EntityTrackerResponse(
                    entityTracker.entities().stream()
                        .map(uuid -> EntityResolver.toEntityRef(EntityResolver.resolveEntityByUUID(server, uuid)))
                        .toList()
                );
            });
    }
}
