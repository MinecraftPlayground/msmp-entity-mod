package dev.loat.msmp_entity.msmp.endpoints.health.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.health.notification.changed.NotificationHealthChanged;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTrackerResponse;

/**
 * Registers the {@code entity:health/changed} MSMP method.
 *
 * <p>Returns a list with all tracked entities for the health changed event</p>
 */
public class HealthChanged {
    
    private HealthChanged() {}

    public static void register(MSMPNamespace namespace) {
        
        namespace.method("health/changed")
            .description("Returns a list of all tracked entities for the health changed event")
            .responseSchema(EntityTrackerResponse.SCHEMA)
            .register((server, client) -> {
                EntityTracker entityTracker = EntityTracker.get(NotificationHealthChanged.TRACKER_KEY);

                return new EntityTrackerResponse(
                    entityTracker.entities().stream()
                        .map(uuid -> EntityResolver.toEntityRef(EntityResolver.resolveEntityByUUID(server, uuid)))
                        .toList()
                );
            });
    }
}
