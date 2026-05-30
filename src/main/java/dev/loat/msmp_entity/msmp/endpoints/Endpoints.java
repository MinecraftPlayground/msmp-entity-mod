package dev.loat.msmp_entity.msmp.endpoints;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.msmp.endpoints.dimension.Dimension;
import dev.loat.msmp_entity.msmp.endpoints.dimension.changed.add.DimensionChangedAdd;
import dev.loat.msmp_entity.msmp.endpoints.dimension.changed.remove.DimensionChangedRemove;
import dev.loat.msmp_entity.msmp.endpoints.dimension.notification.changed.DimensionChanged;
import dev.loat.msmp_entity.msmp.endpoints.dimension.set.DimensionSet;

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
     */
    public static void register(MSMPNamespace namespace, MSMPServer msmpServer) {
        Dimension.register(namespace);
        DimensionSet.register(namespace);
        DimensionChanged.register(namespace, msmpServer);
        DimensionChangedAdd.register(namespace);
        DimensionChangedRemove.register(namespace);
    }
}
