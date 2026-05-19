package dev.loat.msmp_entity_data.msmp.methods.health;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.logging.Logger;
import dev.loat.msmp_entity_data.msmp.components.EntityResolver;
import dev.loat.msmp_entity_data.msmp.methods.health.HealthResponse;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;


/**
 * Registers the {@code entity_data:health/set} MSMP method.
 *
 * <p>Partially updates the health and/or maximum health of any online {@link LivingEntity}.
 * Only the fields provided in the request are changed; omitted fields remain unchanged.
 * Returns the actual values after the update.</p>
 *
 * <p>Example requests:</p>
 * <pre>{@code
 * // Set only health:
 * { "jsonrpc": "2.0", "id": 1, "method": "entity_data:health/set",
 *   "params": [{ "name": "Steve", "health": 15.0 }] }
 *
 * // Set only max_health:
 * { "jsonrpc": "2.0", "id": 1, "method": "entity_data:health/set",
 *   "params": [{ "name": "Steve", "max_health": 40.0 }] }
 *
 * // Set both:
 * { "jsonrpc": "2.0", "id": 1, "method": "entity_data:health/set",
 *   "params": [{ "name": "Steve", "health": 15.0, "max_health": 40.0 }] }
 * }</pre>
 *
 * <p>Example response:</p>
 * <pre>{@code
 * { "entity": { "id": "069a...", "name": "Steve" }, "health": 15.0, "max_health": 40.0 }
 * }</pre>
 */
public class HealthSet {

    /**
     * Registers the {@code entity_data:health/set} method on the given {@link MSMPNamespace}.
     *
     * <p>If {@code max_health} is set, the {@link Attributes#MAX_HEALTH} attribute base value
     * is updated. If the current health exceeds the new maximum, it is clamped automatically
     * by Minecraft. If {@code health} is set, it is applied after {@code max_health} to ensure
     * the value is within the valid range.</p>
     *
     * <p>Throws {@link IllegalArgumentException} if neither {@code health} nor
     * {@code max_health} is provided, or if the entity is not a {@link LivingEntity}.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("health/set",
            HealthSetRequest.SCHEMA,
            HealthResponse.SCHEMA,
            "Partially updates the health and/or maximum health of any LivingEntity",
            (server, params, client) -> {
                if (params.health().isEmpty() && params.maxHealth().isEmpty()) {
                    Logger.warning("entity_data:health/set - neither 'health' nor 'max_health' provided");
                    throw new IllegalArgumentException("Either 'health' or 'max_health' must be provided");
                }

                try {
                    LivingEntity living = EntityResolver.resolveLivingEntity(server, params);

                    if (params.maxHealth().isPresent()) {
                        AttributeInstance attr = living.getAttribute(Attributes.MAX_HEALTH);
                        if (attr == null) {
                            throw new IllegalArgumentException(
                                "Entity %s has no MAX_HEALTH attribute".formatted(living.getUUID())
                            );
                        }
                        attr.setBaseValue(params.maxHealth().get());
                    }

                    if (params.health().isPresent()) {
                        living.setHealth(params.health().get().floatValue());
                    }

                    return new HealthResponse(
                        EntityResolver.toEntityRef(living),
                        living.getHealth(),
                        living.getAttributeValue(Attributes.MAX_HEALTH)
                    );
                } catch (IllegalArgumentException e) {
                    Logger.warning("entity_data:health/set - " + e.getMessage());
                    throw e;
                }
            }
        );
    }
}
