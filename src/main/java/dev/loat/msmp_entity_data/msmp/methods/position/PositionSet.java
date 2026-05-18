package dev.loat.msmp_entity_data.msmp.methods.position;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.logging.Logger;
import dev.loat.msmp_entity_data.msmp.components.EntityResolver;
import dev.loat.msmp_entity_data.msmp.methods.position.PositionResponse;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.List;

/**
 * Registers the {@code entity_data:position/set} MSMP method.
 *
 * <p>Teleports the entity to the given position within its current dimension.</p>
 *
 * <p>Example request:</p>
 * <pre>{@code
 * { "jsonrpc": "2.0", "id": 1, "method": "entity_data:position/set",
 *   "params": [{ "name": "Steve", "position": [100.0, 64.0, 200.0] }] }
 * }</pre>
 *
 * <p>Example response:</p>
 * <pre>{@code
 * { "entity": { "id": "069a...", "name": "Steve" }, "position": [100.0, 64.0, 200.0] }
 * }</pre>
 */
public class PositionSet {

    /**
     * Registers the {@code entity_data:position/set} method on the given {@link MSMPNamespace}.
     *
     * <p>The {@code position} array must contain exactly 3 elements: {@code [x, y, z]}.
     * The entity stays in its current dimension. Rotation is preserved.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("position/set",
            PositionSetRequest.SCHEMA,
            PositionResponse.SCHEMA,
            "Teleports any loaded entity to the given position within its current dimension",
            (server, params, client) -> {
                List<Double> pos = params.position();
                if (pos.size() != 3) {
                    throw new IllegalArgumentException(
                        "'position' must contain exactly 3 elements [x, y, z], got " + pos.size()
                    );
                }

                try {
                    Entity entity = EntityResolver.resolveEntity(server, params);

                    entity.teleportTo(
                        (ServerLevel) entity.level(),
                        pos.get(0), pos.get(1), pos.get(2),
                        java.util.Set.of(),
                        entity.getYRot(), entity.getXRot(),
                        true
                    );

                    return new PositionResponse(
                        EntityResolver.toEntityRef(entity),
                        List.of(entity.getX(), entity.getY(), entity.getZ())
                    );
                } catch (IllegalArgumentException e) {
                    Logger.warning("entity_data:position/set - " + e.getMessage());
                    throw e;
                }
            }
        );
    }
}
