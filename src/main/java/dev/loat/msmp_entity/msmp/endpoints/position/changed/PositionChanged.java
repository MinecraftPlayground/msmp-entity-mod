package dev.loat.msmp_entity.msmp.endpoints.position.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.position.notification.changed.NotificationPositionChanged;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTrackerResponse;

/**
 * Registers the {@code entity:position/changed} MSMP method.
 *
 * <p>Returns a list with all tracked entities for the position changed event</p>
 */
public class PositionChanged {
    
    private PositionChanged() {}

    public static void register(MSMPNamespace namespace) {
        
        namespace.method("position/changed")
            .description("Returns a list of all tracked entities for the position changed event")
            .responseSchema(EntityTrackerResponse.SCHEMA)
            .register((server, client) -> {
                EntityTracker entityTracker = EntityTracker.get(NotificationPositionChanged.TRACKER_KEY);

                return new EntityTrackerResponse(
                    entityTracker.entities().stream()
                        .map(uuid -> EntityResolver.toEntityRef(EntityResolver.resolveEntityByUUID(server, uuid)))
                        .toList()
                );
            });
    }
}
