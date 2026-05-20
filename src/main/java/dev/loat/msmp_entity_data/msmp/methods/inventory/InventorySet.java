package dev.loat.msmp_entity_data.msmp.methods.inventory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.logging.Logger;
import dev.loat.msmp_entity_data.msmp.components.EntityResolver;
import dev.loat.msmp_entity_data.msmp.methods.inventory.InventoryResponse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


/**
 * Registers the {@code entity_data:inventory/set} MSMP method.
 *
 * <p>Partially updates an online player's inventory using a diff approach —
 * only the provided slots are modified, all others remain unchanged.
 * Returns the full inventory state after the update.</p>
 *
 * <p>To clear a slot, provide {@code "id": "minecraft:air"}.</p>
 *
 * <p>Example request:</p>
 * <pre>{@code
 * {
 *   "jsonrpc": "2.0", "id": 1, "method": "entity_data:inventory/set",
 *   "params": [{
 *     "name": "Steve",
 *     "inventory": [
 *       { "Slot": 0, "id": "minecraft:diamond_sword", "count": 1 },
 *       { "Slot": 9, "id": "minecraft:air", "count": 0 }
 *     ]
 *   }]
 * }
 * }</pre>
 *
 * <p>Example response:</p>
 * <pre>{@code
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "inventory": [ ... ]
 * }
 * }</pre>
 */
public class InventorySet {

    /**
     * Registers the {@code entity_data:inventory/set} method on the given {@link MSMPNamespace}.
     *
     * <p>Each entry in the {@code inventory} array must contain a {@code Slot} key (integer).
     * The {@code Slot} key is stripped before passing the entry to {@link ItemStack#CODEC} for
     * deserialization. Invalid slot indices or malformed item data throw
     * {@link IllegalArgumentException}.</p>
     *
     * <p>After all updates are applied, the full inventory is re-serialized and returned.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("inventory/set",
            InventorySetRequest.SCHEMA,
            InventoryResponse.SCHEMA,
            "Partially updates an online player's inventory using a diff approach",
            (server, params, client) -> {
                InventorySetRequest req = params;
                try {
                    Player player = EntityResolver.resolvePlayer(server, req);
                    net.minecraft.world.entity.player.Inventory inv = player.getInventory();
                    int containerSize = inv.getContainerSize();
                    var ctx = server.registryAccess().createSerializationContext(JsonOps.INSTANCE);

                    for (JsonElement element : req.inventory().getAsJsonArray()) {
                        if (!element.isJsonObject()) {
                            throw new IllegalArgumentException("Each inventory entry must be a JSON object");
                        }

                        JsonObject entry = element.getAsJsonObject().deepCopy();

                        if (!entry.has("Slot")) {
                            throw new IllegalArgumentException("Each inventory entry must contain a 'Slot' key");
                        }

                        int slot = entry.get("Slot").getAsInt();
                        if (slot < 0 || slot >= containerSize) {
                            throw new IllegalArgumentException(
                                "Invalid slot index %d — must be between 0 and %d".formatted(slot, containerSize - 1)
                            );
                        }

                        entry.remove("Slot");

                        // Clear the slot if no id given or id is minecraft:air
                        boolean isEmpty = !entry.has("id")
                            || entry.get("id").getAsString().equals("minecraft:air");

                        ItemStack stack = isEmpty ? ItemStack.EMPTY : ItemStack.CODEC
                            .decode(ctx, entry)
                            .getOrThrow(err -> new IllegalArgumentException(
                                "Failed to deserialize item in slot %d: %s".formatted(slot, err)
                            ))
                            .getFirst();

                        inv.setItem(slot, stack);
                    }

                    // Re-serialize full inventory
                    JsonArray inventory = new JsonArray();
                    for (int slot = 0; slot < containerSize; slot++) {
                        ItemStack stack = inv.getItem(slot);
                        if (stack.isEmpty()) continue;

                        final int finalSlot = slot;
                        JsonElement itemJson = ItemStack.CODEC
                            .encodeStart(ctx, stack)
                            .getOrThrow(err -> new IllegalStateException(
                                "Failed to serialize item in slot %d: %s".formatted(finalSlot, err)
                            ));

                        JsonObject responseEntry = itemJson.getAsJsonObject().deepCopy();
                        responseEntry.addProperty("Slot", slot);
                        inventory.add(responseEntry);
                    }

                    return new InventoryResponse(EntityResolver.toEntityRef(player), inventory);
                } catch (IllegalArgumentException e) {
                    Logger.warning("entity_data:inventory/set - " + e.getMessage());
                    throw e;
                }
            }
        );
    }
}
