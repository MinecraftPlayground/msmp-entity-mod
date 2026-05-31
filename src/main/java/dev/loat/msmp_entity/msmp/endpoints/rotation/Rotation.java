package dev.loat.msmp_entity.msmp.endpoints.rotation;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.Logger;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;

import net.minecraft.world.entity.Entity;

import java.util.List;


/**
 * Registers the {@code entity:rotation} MSMP method.
 *
 * <p>Returns the current rotation of any loaded entity.
 * Players can be looked up by UUID or name; all other entities require a UUID.</p>
 *
 * <p>Example request:</p>
 * <pre><code>
 * {
 *   "jsonrpc": "2.0",
 *   "id": 1,
 *   "method": "entity:rotation",
 *   "params": [{ "name": "Steve" }]
 * }
 * </code></pre>
 *
 * <p>Example response:</p>
 * <pre><code>
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "rotation": [90.0, -15.0]
 * }
 * </code></pre>
 */
public class Rotation {

    private Rotation() {}

    /**
     * Registers the {@code entity:rotation} method on the given {@link MSMPNamespace}.
     *
     * <p>Entity lookup is delegated to {@link EntityResolver#resolveEntity}.
     * Rotation is returned as a {@code [yaw, pitch]} array.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method(
            "rotation",
            EntityRequest.SCHEMA,
            RotationResponse.SCHEMA,
            "Returns the current rotation of any loaded entity by UUID, or a player by name",
            (server, params, client) -> {
                try {
                    Entity entity = EntityResolver.resolveEntity(server, params);
                    return new RotationResponse(
                        EntityResolver.toEntityRef(entity),
                        List.of((double) entity.getYRot(), (double) entity.getXRot())
                    );
                } catch (IllegalArgumentException e) {
                    Logger.warning("entity:rotation - " + e.getMessage());
                    throw e;
                }
            }
        );
    }
}
