package dev.loat.msmp_entity_data.msmp.methods.position;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.logging.Logger;
import dev.loat.msmp_entity_data.msmp.components.EntityRequest;
import dev.loat.msmp_entity_data.msmp.components.EntityResolver;
import net.minecraft.world.entity.Entity;

import java.util.List;

/**
 * Registers the {@code entity_data:position} MSMP method.
 *
 * <p>Returns the current position of any loaded entity.
 * Players can be looked up by UUID or name; all other entities require a UUID.</p>
 *
 * <p>Example request:</p>
 * <pre>{@code
 * { "jsonrpc": "2.0", "id": 1, "method": "entity_data:position",
 *   "params": [{ "name": "Steve" }] }
 * }</pre>
 *
 * <p>Example response:</p>
 * <pre>{@code
 * { "entity": { "id": "069a...", "name": "Steve" }, "position": [128.5, 64.0, -32.3] }
 * }</pre>
 */
public class Position {

    /**
     * Registers the {@code entity_data:position} method on the given {@link MSMPNamespace}.
     *
     * <p>Entity lookup is delegated to {@link EntityResolver#resolveEntity}.
     * Position is returned as a {@code [x, y, z]} array.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("position",
            EntityRequest.SCHEMA,
            PositionResponse.SCHEMA,
            "Returns the current position of any loaded entity by UUID, or a player by name",
            (server, params, client) -> {
                try {
                    Entity entity = EntityResolver.resolveEntity(server, params);
                    return new PositionResponse(
                        EntityResolver.toEntityRef(entity),
                        List.of(entity.getX(), entity.getY(), entity.getZ())
                    );
                } catch (IllegalArgumentException e) {
                    Logger.warning("entity_data:position - " + e.getMessage());
                    throw e;
                }
            }
        );
    }
}
