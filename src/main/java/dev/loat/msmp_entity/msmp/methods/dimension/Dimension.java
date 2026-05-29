package dev.loat.msmp_entity.msmp.methods.dimension;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.Logger;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import net.minecraft.world.entity.Entity;


/**
 * Registers the {@code entity:dimension} MSMP method.
 *
 * <p>Returns the dimension of any loaded entity.
 * Players can be looked up by UUID or name; all other entities require a UUID.</p>
 *
 * <p>Example request:</p>
 * <pre>{@code
 * { "jsonrpc": "2.0", "id": 1, "method": "entity:dimension",
 *   "params": [{ "name": "Steve" }] }
 * }</pre>
 *
 * <p>Example response:</p>
 * <pre>{@code
 * { "entity": { "id": "069a...", "name": "Steve" }, "dimension": "minecraft:overworld" }
 * }</pre>
 */
public class Dimension {

    private Dimension() {}

    /**
     * Registers the {@code entity:dimension} method on the given {@link MSMPNamespace}.
     *
     * <p>Entity lookup is delegated to {@link EntityResolver#resolveEntity}.
     * The dimension is returned as a resource key string via {@code identifier().toString()},
     * e.g. {@code minecraft:overworld}, {@code minecraft:the_nether}, {@code minecraft:the_end}.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("dimension",
            EntityRequest.SCHEMA,
            DimensionResponse.SCHEMA,
            "Returns the current dimension of any loaded entity by UUID, or a player by name",
            (server, params, client) -> {
                try {
                    Entity entity = EntityResolver.resolveEntity(server, params);
                    return new DimensionResponse(
                        EntityResolver.toEntityRef(entity),
                        entity.level().dimension().identifier().toString()
                    );
                } catch (IllegalArgumentException e) {
                    Logger.warning("entity:dimension - " + e.getMessage());
                    throw e;
                }
            }
        );
    }
}
