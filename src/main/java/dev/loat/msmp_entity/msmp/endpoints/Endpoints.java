package dev.loat.msmp_entity.msmp.endpoints;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.msmp.endpoints.dimension.Dimension;
import dev.loat.msmp_entity.msmp.endpoints.dimension.DimensionSet;
import dev.loat.msmp_entity.msmp.endpoints.dimension.changed.DimensionChanged;
import dev.loat.msmp_entity.msmp.endpoints.dimension.changed.DimensionChangedAdd;
import dev.loat.msmp_entity.msmp.endpoints.dimension.changed.DimensionChangedRemove;
import dev.loat.msmp_entity.msmp.endpoints.dimension.notification.changed.NotificationDimensionChanged;
import dev.loat.msmp_entity.msmp.endpoints.health.Health;
import dev.loat.msmp_entity.msmp.endpoints.health.HealthSet;
import dev.loat.msmp_entity.msmp.endpoints.health.changed.HealthChangedAdd;
import dev.loat.msmp_entity.msmp.endpoints.health.changed.HealthChangedRemove;
import dev.loat.msmp_entity.msmp.endpoints.health.notification.changed.NotificationHealthChanged;
import dev.loat.msmp_entity.msmp.endpoints.items.Items;
import dev.loat.msmp_entity.msmp.endpoints.items.ItemsSet;
import dev.loat.msmp_entity.msmp.endpoints.position.Position;
import dev.loat.msmp_entity.msmp.endpoints.position.PositionSet;
import dev.loat.msmp_entity.msmp.endpoints.position.changed.PositionChangedAdd;
import dev.loat.msmp_entity.msmp.endpoints.position.changed.PositionChangedRemove;
import dev.loat.msmp_entity.msmp.endpoints.position.notification.changed.NotificationPositionChanged;
import dev.loat.msmp_entity.msmp.endpoints.rotation.Rotation;
import dev.loat.msmp_entity.msmp.endpoints.rotation.RotationSet;
import dev.loat.msmp_entity.msmp.endpoints.saturation.Saturation;
import dev.loat.msmp_entity.msmp.endpoints.saturation.SaturationSet;
import dev.loat.msmp_entity.msmp.endpoints.uuid.UUID;

import java.util.function.Supplier;


/**
 * Central registration point for all {@code entity} MSMP endpoints.
 * 
 * <p>Each endpoint is implemented in its own sub-package and registered here.</p>
 */
public class Endpoints {
    private Endpoints() {}

    /**
     * Registers all endpoints on the given {@link MSMPNamespace}.
     *
     * @param namespace The namespace to register all endpoints under
     * @param msmpServer A supplier for the MSMPServer instance, used by some endpoints to subscribe to server events
     */
    public static void register(MSMPNamespace namespace, Supplier<MSMPServer> msmpServer) {
        Dimension.register(namespace);
        DimensionSet.register(namespace);
        DimensionChanged.register(namespace);
        DimensionChangedAdd.register(namespace);
        DimensionChangedRemove.register(namespace);
        NotificationDimensionChanged.register(namespace, msmpServer);

        Health.register(namespace);
        HealthSet.register(namespace);
        HealthChangedAdd.register(namespace);
        HealthChangedRemove.register(namespace);
        NotificationHealthChanged.register(namespace, msmpServer);

        Items.register(namespace);
        ItemsSet.register(namespace);

        Position.register(namespace);
        PositionSet.register(namespace);
        PositionChangedAdd.register(namespace);
        PositionChangedRemove.register(namespace);
        NotificationPositionChanged.register(namespace, msmpServer);

        Rotation.register(namespace);
        RotationSet.register(namespace);

        Saturation.register(namespace);
        SaturationSet.register(namespace);

        UUID.register(namespace);
    }
}
