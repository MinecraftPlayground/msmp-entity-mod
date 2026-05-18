package dev.loat.msmp_entity_data.msmp.methods.rotation;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.logging.Logger;
import dev.loat.msmp_entity_data.msmp.components.EntityResolver;
import dev.loat.msmp_entity_data.msmp.methods.rotation.RotationResponse;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.List;


/**
 * Registers the {@code entity_data:rotation/set} MSMP method.
 *
 * <p>Sets the rotation of any loaded entity. Position and dimension are preserved.</p>
 *
 * <p>Example request:</p>
 * <pre>{@code
 * { "jsonrpc": "2.0", "id": 1, "method": "entity_data:rotation/set",
 *   "params": [{ "name": "Steve", "rotation": [90.0, -15.0] }] }
 * }</pre>
 *
 * <p>Example response:</p>
 * <pre>{@code
 * { "entity": { "id": "069a...", "name": "Steve" }, "rotation": [90.0, -15.0] }
 * }</pre>
 */
public class RotationSet {

    /**
     * Registers the {@code entity_data:rotation/set} method on the given {@link MSMPNamespace}.
     *
     * <p>The {@code rotation} array must contain exactly 2 elements: {@code [yaw, pitch]}.
     * Position and dimension are preserved.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("rotation/set",
            RotationSetRequest.SCHEMA,
            RotationResponse.SCHEMA,
            "Sets the rotation of any loaded entity, preserving its position and dimension",
            (server, params, client) -> {
                List<Double> rot = params.rotation();
                if (rot.size() != 2) {
                    throw new IllegalArgumentException(
                        "'rotation' must contain exactly 2 elements [yaw, pitch], got " + rot.size()
                    );
                }

                try {
                    Entity entity = EntityResolver.resolveEntity(server, params);

                    float yaw = rot.get(0).floatValue();
                    float pitch = rot.get(1).floatValue();

                    entity.teleportTo(
                        (ServerLevel) entity.level(),
                        entity.getX(), entity.getY(), entity.getZ(),
                        java.util.Set.of(),
                        yaw, pitch,
                        true
                    );

                    return new RotationResponse(
                        EntityResolver.toEntityRef(entity),
                        List.of((double) entity.getYRot(), (double) entity.getXRot())
                    );
                } catch (IllegalArgumentException e) {
                    Logger.warning("entity_data:rotation/set - " + e.getMessage());
                    throw e;
                }
            }
        );
    }
}
