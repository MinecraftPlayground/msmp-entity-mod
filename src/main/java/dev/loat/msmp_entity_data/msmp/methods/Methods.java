package dev.loat.msmp_entity_data.msmp.methods;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.msmp.methods.dimension.Dimension;
import dev.loat.msmp_entity_data.msmp.methods.dimension.DimensionSet;
import dev.loat.msmp_entity_data.msmp.methods.health.Health;
import dev.loat.msmp_entity_data.msmp.methods.health.HealthSet;
import dev.loat.msmp_entity_data.msmp.methods.inventory.Inventory;
import dev.loat.msmp_entity_data.msmp.methods.inventory.InventorySet;
import dev.loat.msmp_entity_data.msmp.methods.position.Position;
import dev.loat.msmp_entity_data.msmp.methods.position.PositionSet;
import dev.loat.msmp_entity_data.msmp.methods.rotation.Rotation;
import dev.loat.msmp_entity_data.msmp.methods.rotation.RotationSet;
import dev.loat.msmp_entity_data.msmp.methods.uuid.UUID;


/**
 * Central registration point for all {@code entity_data} MSMP methods.
 *
 * <p>Each method is implemented in its own sub-package and registered here.
 * Call {@link #register(MSMPNamespace)} once during mod initialization, before
 * the server starts.</p>
 */
public class Methods {

    private Methods() {}

    /**
     * Registers all {@code entity_data} methods on the given {@link MSMPNamespace}.
     *
     * @param namespace The namespace to register all methods under
     */
    public static void register(MSMPNamespace namespace) {
        Dimension.register(namespace);
        DimensionSet.register(namespace);
        Health.register(namespace);
        HealthSet.register(namespace);
        Inventory.register(namespace);
        InventorySet.register(namespace);
        Position.register(namespace);
        PositionSet.register(namespace);
        Rotation.register(namespace);
        RotationSet.register(namespace);
        UUID.register(namespace);
    }
}
