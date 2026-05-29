package dev.loat.msmp_entity.msmp.methods.saturation;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.Logger;
import dev.loat.msmp_entity.msmp.components.EntityRequest;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import net.minecraft.world.entity.player.Player;


/**
 * Registers the {@code entity:saturation} MSMP method.
 *
 * <p>Returns the current food level and saturation of an online player.</p>
 *
 * <p>Example request:</p>
 * <pre>{@code
 * { "jsonrpc": "2.0", "id": 1, "method": "entity:saturation",
 *   "params": [{ "name": "Steve" }] }
 * }</pre>
 *
 * <p>Example response:</p>
 * <pre>{@code
 * { "entity": { "id": "069a...", "name": "Steve" }, "food": 18, "saturation": 5.0 }
 * }</pre>
 */
public class Saturation {

    private Saturation() {}

    /**
     * Registers the {@code entity:saturation} method on the given {@link MSMPNamespace}.
     *
     * <p>Entity lookup is delegated to {@link EntityResolver#resolvePlayer} since only
     * players have food data. Food level and saturation are read from
     * {@link net.minecraft.world.food.FoodData}.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("saturation",
            EntityRequest.SCHEMA,
            SaturationResponse.SCHEMA,
            "Returns the current food level and saturation of an online player",
            (server, params, client) -> {
                try {
                    Player player = EntityResolver.resolvePlayer(server, params);
                    return new SaturationResponse(
                        EntityResolver.toEntityRef(player),
                        player.getFoodData().getFoodLevel(),
                        player.getFoodData().getSaturationLevel()
                    );
                } catch (IllegalArgumentException e) {
                    Logger.warning("entity:saturation - " + e.getMessage());
                    throw e;
                }
            }
        );
    }
}
