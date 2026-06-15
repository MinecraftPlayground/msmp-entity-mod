package dev.loat.msmp_entity.msmp.endpoints.dimension.changed;

import java.util.List;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.msmp.components.EntityRef;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.dimension.notification.changed.NotificationDimensionChanged;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTrackerResponse;


/**
 * Registers the {@code entity:dimension/changed} MSMP method.
 *
 * <p>Returns a list with all tracked entities for the dimension changed event</p>
 */
public class DimensionChangedClear {
    
    private DimensionChangedClear() {}

    public static void register(MSMPNamespace namespace) {
        
        namespace.method("dimension/changed/clear")
            .description("Clear all entities for the dimension changed event")
            .responseSchema(EntityTrackerResponse.SCHEMA)
            .register((server, client) -> {
                EntityTracker entityTracker = EntityTracker.get(NotificationDimensionChanged.TRACKER_KEY);

                List<EntityRef> entities = entityTracker.entities().stream()
                    .map(uuid -> EntityResolver.toEntityRef(EntityResolver.resolveEntityByUUID(server, uuid)))
                    .toList();

                entityTracker.removeAll();

                return new EntityTrackerResponse(entities);
            });
    }
}
