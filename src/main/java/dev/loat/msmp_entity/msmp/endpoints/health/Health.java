package dev.loat.msmp_entity.msmp.endpoints.health;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.Logger;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;


/**
 * Registers the {@code entity:health} MSMP method.
 *
 * <p>Returns the current and maximum health of any online {@link LivingEntity}.
 * Players can be looked up by UUID or name; all other entities require a UUID.</p>
 *
 * <p>Example request:</p>
 * <pre><code>
 * {
 *   "jsonrpc": "2.0",
 *   "id": 1,
 *   "method": "entity:health",
 *   "params": [{ "name": "Steve" }]
 * }
 * </code></pre>
 *
 * <p>Example response:</p>
 * <pre><code>
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "health": 20.0,
 *   "max_health": 20.0
 * }
 * </code></pre>
 */
public class Health {

    private Health() {}

    /**
     * Registers the {@code entity:health} method on the given {@link MSMPNamespace}.
     *
     * <p>Entity lookup is delegated to {@link EntityResolver#resolveLivingEntity}.
     * Max health is read from the {@link Attributes#MAX_HEALTH} attribute as a
     * {@code double} to avoid floating-point precision issues with {@code float}.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method(
            "health",
            EntityRequest.SCHEMA,
            HealthResponse.SCHEMA,
            "Returns the current and maximum health of any LivingEntity",
            (server, params, client) -> {
                try {
                    LivingEntity living = EntityResolver.resolveLivingEntity(server, params);
                    return new HealthResponse(
                        EntityResolver.toEntityRef(living),
                        living.getHealth(),
                        living.getAttributeValue(Attributes.MAX_HEALTH)
                    );
                } catch (IllegalArgumentException e) {
                    Logger.warning("entity:health - " + e.getMessage());
                    return null;
                }
            }
        );
    }
}
