package dev.loat.msmp_entity.msmp.endpoints.dimension.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.dimension.notification.changed.NotificationDimensionChanged;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTrackerResponse;

/**
 * Registers the {@code entity:dimension/changed} MSMP method.
 *
 * <p>Returns a list with all tracked entities for the dimension changed event</p>
 */
public class DimensionChanged {
    
    private DimensionChanged() {}

    public static void register(MSMPNamespace namespace) {
        
        namespace.method("dimension/changed")
            .description("Returns a list of all tracked entities for the dimension changed event")
            .responseSchema(EntityTrackerResponse.SCHEMA)
            .register((server, client) -> {
                EntityTracker entityTracker = EntityTracker.get(NotificationDimensionChanged.TRACKER_KEY);

                return new EntityTrackerResponse(
                    entityTracker.entities().stream()
                        .map(uuid -> EntityResolver.toEntityRef(EntityResolver.resolveEntityByUUID(server, uuid)))
                        .toList()
                );
            });
    }
}
