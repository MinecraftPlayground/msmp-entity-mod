package dev.loat.msmp_entity.msmp.endpoints.uuid;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity.logging.Logger;
import dev.loat.msmp_entity.msmp.components.PlayerRef;
import net.minecraft.server.level.ServerPlayer;


/**
 * Registers the {@code entity:uuid} MSMP method.
 *
 * <p>Returns the {@link PlayerRef} of an online player looked up by name,
 * which always includes the UUID and the confirmed in-game name.</p>
 *
 * <p>Example request:</p>
 * <pre>
 * {@code {
 *   "jsonrpc": "2.0", "id": 1, "method": "entity:uuid",
 *   "params": [{ "name": "Steve" }]
 * }
 * }</pre>
 *
 * <p>Example response:</p>
 * <pre>
 * {@code
 * { "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5", "name": "Steve" }
 * }</pre>
 */
public class UUID {

    private UUID() {}

    /**
     * Registers the {@code entity:uuid} method on the given {@link MSMPNamespace}.
     *
     * <p>Looks up the player via {@code server.getPlayerList().getPlayerByName(String)}.
     * Only works for online players. Throws {@link IllegalArgumentException} if no
     * player with the given name is found.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("uuid")
            .description("Returns the UUID of an online player by name")
            .requestSchema(UUIDRequest.SCHEMA)
            .responseSchema(PlayerRef.SCHEMA)
            .register((server, client, params) -> {
                ServerPlayer player = server.getPlayerList().getPlayerByName(params.name());
                if (player == null) {
                    Logger.warning("entity:uuid - player not found: " + params.name());
                    throw new IllegalArgumentException("Player not found: " + params.name());
                }
                return new PlayerRef(
                    player.getUUID().toString(),
                    player.getName().getString()
                );
            });
    }
}
