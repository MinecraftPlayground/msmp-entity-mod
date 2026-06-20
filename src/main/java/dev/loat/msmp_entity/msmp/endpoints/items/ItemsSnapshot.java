package dev.loat.msmp_entity.msmp.endpoints.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


/**
 * Utility for building a combined JSON snapshot of a player's inventory and equipment
 * in the same Vanilla NBT format used by {@code entity:items}.
 *
 * <p>Used by the {@code entity:items} method's response and by the items-changed
 * notification system, which compares two snapshots via {@link JsonObject#equals(Object)}
 * to detect changes.</p>
 */
public final class ItemsSnapshot {

    private ItemsSnapshot() {}

    /**
     * Builds a snapshot of the given player's inventory and equipment.
     *
     * @param server The running {@link MinecraftServer} instance, used for item serialization
     * @param player The player whose inventory and equipment should be captured
     * @return A {@link JsonObject} with {@code inventory} (array) and {@code equipment} (object) keys
     */
    public static JsonObject of(MinecraftServer server, Player player) {
        Inventory items = player.getInventory();

        JsonArray inventoryItems = new JsonArray();
        JsonObject equipmentItems = new JsonObject();

        for (int slot = 0; slot < items.getContainerSize(); slot++) {
            ItemStack stack = items.getItem(slot);
            if (stack.isEmpty()) continue;

            final int finalSlot = slot;
            JsonElement itemJson = ItemStack.CODEC
                .encodeStart(server.registryAccess().createSerializationContext(JsonOps.INSTANCE), stack)
                .getOrThrow(err -> new IllegalStateException(
                    "Failed to serialize item in slot %d: %s".formatted(finalSlot, err)
                ));

            JsonObject entry = itemJson.getAsJsonObject().deepCopy();
            entry.addProperty("count", entry.has("count") ? entry.get("count").getAsInt() : 1);

            if (slot >= 36 && slot <= 40) {
                String equipmentKey = switch (slot) {
                    case 36 -> "feet";
                    case 37 -> "legs";
                    case 38 -> "chest";
                    case 39 -> "head";
                    case 40 -> "offhand";
                    default -> null;
                };
                if (equipmentKey != null) {
                    equipmentItems.add(equipmentKey, entry);
                }
            } else {
                entry.addProperty("Slot", slot);
                inventoryItems.add(entry);
            }
        }

        JsonObject combined = new JsonObject();
        combined.add("inventory", inventoryItems);
        combined.add("equipment", equipmentItems);
        return combined;
    }
}
