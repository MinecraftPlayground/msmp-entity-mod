package dev.loat.msmp_entity_data.msmp.methods.inventory;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp_entity_data.logging.Logger;
import dev.loat.msmp_entity_data.msmp.components.EntityResolver;
import dev.loat.msmp_entity_data.msmp.methods.inventory.InventoryResponse;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Registers the {@code entity_data:inventory/set} MSMP method.
 *
 * <p>Partially updates an online player\'s inventory using a diff approach —
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
     * Entries are converted from JSON to NBT via {@link NbtOps}, then deserialized via
     * {@link ItemStack#parse}. Invalid slot indices or malformed item data throw
     * {@link IllegalArgumentException}.</p>
     *
     * <p>After all updates are applied, the full inventory is re-serialized via
     * {@link ContainerHelper#saveAllItems} and returned.</p>
     *
     * @param namespace The namespace to register this method under
     */
    public static void register(MSMPNamespace namespace) {
        namespace.method("inventory/set",
            InventorySetRequest.SCHEMA,
            InventoryResponse.SCHEMA,
            "Partially updates an online player\'s inventory using a diff approach",
            (server, params, client) -> {
                try {
                    Player player = EntityResolver.resolvePlayer(server, params);
                    net.minecraft.world.entity.player.Inventory inv = player.getInventory();
                    int containerSize = inv.getContainerSize();

                    for (JsonElement element : params.inventory().getAsJsonArray()) {
                        Tag nbtEntry = JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, element);

                        if (!(nbtEntry instanceof net.minecraft.nbt.CompoundTag compound)) {
                            throw new IllegalArgumentException(
                                "Each inventory entry must be a JSON object"
                            );
                        }
                        if (!compound.contains("Slot")) {
                            throw new IllegalArgumentException(
                                "Each inventory entry must contain a \'Slot\' key"
                            );
                        }

                        int slot = compound.getInt("Slot");
                        if (slot < 0 || slot >= containerSize) {
                            throw new IllegalArgumentException(
                                "Invalid slot index %d — must be between 0 and %d".formatted(slot, containerSize - 1)
                            );
                        }

                        compound.remove("Slot");
                        ItemStack stack = ItemStack.parse(server.registryAccess(), compound)
                            .orElse(ItemStack.EMPTY);

                        inv.setItem(slot, stack);
                    }

                    ListTag nbt = new ListTag();
                    ContainerHelper.saveAllItems(nbt, inv, server.registryAccess());
                    JsonElement inventory = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, nbt);

                    return new InventoryResponse(EntityResolver.toEntityRef(player), inventory);
                } catch (IllegalArgumentException e) {
                    Logger.warning("entity_data:inventory/set - " + e.getMessage());
                    throw e;
                }
            }
        );
    }
}
