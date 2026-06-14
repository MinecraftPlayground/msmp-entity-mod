package dev.loat.msmp_entity.msmp.endpoints.dimension;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.RPCConnectionLogger;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;

import net.minecraft.world.entity.Entity;


/**
 * Registers the {@code entity:dimension} MSMP method.
 * 
 * <p>Returns the current dimension of any loaded entity.</p>
 * 
 * <p>Example request:</p>
 * <pre><code>
 * {
 *   "jsonrpc": "2.0",
 *   "id": 1,
 *   "method": "entity:dimension",
 *   "params": [{ "name": "Steve" }]
 * }
 * </code></pre>
 *
 * <p>Example response:</p>
 * <pre><code>
 * { 
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "dimension": "minecraft:overworld"
 * }
 * </code></pre>
 */
public class Dimension {

    private Dimension() {}

    /**
     * Registers the {@code entity:dimension} method.
     *
     * <p>The dimension is returned as a resource key string, ex. {@code minecraft:overworld},
     * {@code minecraft:the_nether}, {@code minecraft:the_end}.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        
        namespace.method("dimension")
            .description("Returns the current dimension of any loaded entity by UUID, or a player by name")
            .requestSchema(EntityRequest.SCHEMA)
            .responseSchema(DimensionResponse.SCHEMA)
            .register((server, client, params) -> {
                try {
                    Entity entity = EntityResolver.resolveEntity(server, params);
                    return new DimensionResponse(
                        EntityResolver.toEntityRef(entity),
                        entity.level().dimension().identifier().toString()
                    );
                } catch (IllegalArgumentException e) {
                    RPCConnectionLogger.warning(client.connectionId(), "entity:dimension - " + e.getMessage());
                    throw e;
                }
            });
    }
}
