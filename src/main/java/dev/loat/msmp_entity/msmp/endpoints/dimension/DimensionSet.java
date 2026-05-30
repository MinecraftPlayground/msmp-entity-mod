package dev.loat.msmp_entity.msmp.endpoints.dimension;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.RPCConnectionLogger;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;


/**
 * Registers the {@code entity:dimension/set} MSMP method.
 *
 * <p>Transfers the entity to the given dimension, keeping its current position and rotation.</p>
 *
 * <p>Example request:</p>
 * <pre>{@code
 * {
 *   "jsonrpc": "2.0",
 *   "id": 1,
 *   "method": "entity:dimension/set",
 *   "params": [{ "name": "Steve", "dimension": "minecraft:the_nether" }]
 * }
 * }</pre>
 *
 * <p>Example response:</p>
 * <pre>{@code
 * { 
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "dimension": "minecraft:the_nether"
 * }
 * }</pre>
 */
public class DimensionSet {

    private DimensionSet() {}

    /**
     * Registers the {@code entity:dimension/set} method.
     *
     * <p>The entity is teleported to the target dimension at its current position and rotation.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("dimension/set",
            DimensionSetRequest.SCHEMA,
            DimensionResponse.SCHEMA,
            "Transfers any loaded entity to the given dimension, keeping its current position and rotation",
            (server, params, client) -> {
                try {
                    Entity entity = EntityResolver.resolveEntity(server, params);

                    Identifier dimensionId = Identifier.parse(params.dimension());
                    ResourceKey<Level> dimKey = ResourceKey.create(
                        Registries.DIMENSION,
                        dimensionId
                    );
                    ServerLevel targetLevel = server.getLevel(dimKey);
                    if (targetLevel == null) {
                        throw new IllegalArgumentException("Unknown dimension: " + params.dimension());
                    }

                    entity.teleportTo(
                        targetLevel,
                        entity.getX(), entity.getY(), entity.getZ(),
                        java.util.Set.of(),
                        entity.getYRot(), entity.getXRot(),
                        true
                    );

                    RPCConnectionLogger.info(client.connectionId(), "entity:dimension/set - teleported %s to dimension %s".formatted(entity.getUUID(), params.dimension()));

                    return new DimensionResponse(
                        EntityResolver.toEntityRef(entity),
                        entity.level().dimension().identifier().toString()
                    );
                } catch (IllegalArgumentException e) {
                    RPCConnectionLogger.warning(client.connectionId(), "entity:dimension/set - " + e.getMessage());
                    throw e;
                }
            }
        );
    }
}

