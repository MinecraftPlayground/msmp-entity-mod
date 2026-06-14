package dev.loat.msmp_entity.msmp.endpoints.saturation;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.Logger;
import dev.loat.msmp_entity.msmp.components.EntityResolver;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;


/**
 * Registers the {@code entity:saturation/set} MSMP method.
 *
 * <p>Partially updates the food level and/or saturation of an online player.
 * Only the provided fields are changed; omitted fields remain unchanged.
 * Returns the actual values after the update.</p>
 *
 * <p>Example requests:</p>
 * <pre><code>
 * // Set only food:
 * {
 *   "jsonrpc": "2.0",
 *   "id": 1,
 *   "method": "entity:saturation/set",
 *   "params": [{ "name": "Steve", "food": 20 }]
 * }
 *
 * // Set only saturation:
 * {
 *   "jsonrpc": "2.0",
 *   "id": 1,
 *   "method": "entity:saturation/set",
 *   "params": [{ "name": "Steve", "saturation": 10.0 }]
 * }
 *
 * // Set both:
 * {
 *   "jsonrpc": "2.0",
 *   "id": 1,
 *   "method": "entity:saturation/set",
 *   "params": [{ "name": "Steve", "food": 20, "saturation": 10.0 }]
 * }
 * </code></pre>
 *
 * <p>Example response:</p>
 * <pre><code>
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "food": 20,
 *   "saturation": 10.0
 * }
 * </code></pre>
 */
public class SaturationSet {

    private SaturationSet() {}

    /**
     * Registers the {@code entity:saturation/set} method on the given {@link MSMPNamespace}.
     *
     * <p>Food level is clamped to 0–20 by Minecraft internally.
     * Saturation is clamped to {@code 0.0–foodLevel} by Minecraft internally.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        
        namespace.method("saturation/set")
            .requestSchema(SaturationSetRequest.SCHEMA)
            .responseSchema(SaturationResponse.SCHEMA)
            .description("Partially updates the food level and/or saturation of an online player")
            .register((server, client, params) -> {
                if (params.food().isEmpty() && params.saturation().isEmpty()) {
                    Logger.warning("entity:saturation/set - neither 'food' nor 'saturation' provided");
                    throw new IllegalArgumentException("Either 'food' or 'saturation' must be provided");
                }

                try {
                    Player player = EntityResolver.resolvePlayer(server, params);
                    FoodData foodData = player.getFoodData();

                    if (params.food().isPresent()) {
                        foodData.setFoodLevel(params.food().get());
                    }

                    if (params.saturation().isPresent()) {
                        foodData.setSaturation(params.saturation().get().floatValue());
                    }

                    return new SaturationResponse(
                        EntityResolver.toEntityRef(player),
                        foodData.getFoodLevel(),
                        foodData.getSaturationLevel()
                    );
                } catch (IllegalArgumentException e) {
                    Logger.warning("entity:saturation/set - " + e.getMessage());
                    throw e;
                }
            });
    }
}
