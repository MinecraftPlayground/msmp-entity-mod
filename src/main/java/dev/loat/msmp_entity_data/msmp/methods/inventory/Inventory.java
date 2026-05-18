package dev.loat.msmp_entity_data.msmp.methods.inventory;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.logging.Logger;
import dev.loat.msmp_entity_data.msmp.components.EntityRequest;
import dev.loat.msmp_entity_data.msmp.components.EntityResolver;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;

/**
 * Registers the {@code entity_data:inventory} MSMP method.
 *
 * <p>Returns all occupied inventory slots of an online player in the Vanilla NBT
 * {@code Inventory} format, equivalent to {@code /data get entity @s Inventory}.</p>
 *
 * <p>Slot layout:</p>
 * <ul>
 *   <li>0–8: Hotbar</li>
 *   <li>9–35: Main inventory</li>
 *   <li>36–39: Armor (36 feet, 37 legs, 38 chest, 39 head)</li>
 *   <li>40: Offhand</li>
 * </ul>
 *
 * <p>Example request:</p>
 * <pre>{@code
 * { "jsonrpc": "2.0", "id": 1, "method": "entity_data:inventory",
 *   "params": [{ "name": "Steve" }] }
 * }</pre>
 *
 * <p>Example response:</p>
 * <pre>{@code
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "inventory": [
 *     { "Slot": 0, "id": "minecraft:diamond_sword", "count": 1, "components": { ... } },
 *     { "Slot": 36, "id": "minecraft:iron_boots", "count": 1 }
 *   ]
 * }
 * }</pre>
 */
public class Inventory {

    /**
     * Registers the {@code entity_data:inventory} method on the given {@link MSMPNamespace}.
     *
     * <p>Entity lookup is delegated to {@link EntityResolver#resolvePlayer} since only
     * players have an inventory. The full inventory is serialized via
     * {@link ContainerHelper#saveAllItems} to NBT, then converted to JSON via
     * {@link NbtOps}, matching the Vanilla {@code /data get entity @s Inventory} format.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("inventory",
            EntityRequest.SCHEMA,
            InventoryResponse.SCHEMA,
            "Returns all occupied inventory slots of an online player in Vanilla NBT format",
            (server, params, client) -> {
                try {
                    Player player = EntityResolver.resolvePlayer(server, params);

                    ListTag nbt = new ListTag();
                    ContainerHelper.saveAllItems(nbt, player.getInventory(), server.registryAccess());
                    JsonElement inventory = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, nbt);

                    return new InventoryResponse(EntityResolver.toEntityRef(player), inventory);
                } catch (IllegalArgumentException e) {
                    Logger.warning("entity_data:inventory - " + e.getMessage());
                    throw e;
                }
            }
        );
    }
}
