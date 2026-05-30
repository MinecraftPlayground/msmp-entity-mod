package dev.loat.msmp_entity.msmp.methods;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.msmp.methods.position.Position;
import dev.loat.msmp_entity.msmp.methods.position.PositionSet;
import dev.loat.msmp_entity.msmp.methods.rotation.Rotation;
import dev.loat.msmp_entity.msmp.methods.rotation.RotationSet;
import dev.loat.msmp_entity.msmp.methods.saturation.Saturation;
import dev.loat.msmp_entity.msmp.methods.saturation.SaturationSet;


/**
 * Central registration point for all {@code entity} MSMP methods.
 *
 * <p>Each method is implemented in its own sub-package and registered here.
 * Call {@link #register(MSMPNamespace)} once during mod initialization, before
 * the server starts.</p>
 */
public class Methods {

    private Methods() {}

    /**
     * Registers all {@code entity} methods on the given {@link MSMPNamespace}.
     *
     * @param namespace The namespace to register all methods under
     */
    public static void register(MSMPNamespace namespace) {
        Position.register(namespace);
        PositionSet.register(namespace);
        Rotation.register(namespace);
        RotationSet.register(namespace);
        Saturation.register(namespace);
        SaturationSet.register(namespace);
    }
}
