package dev.loat.msmp_entity_data.msmp.methods.inventory;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.loat.msmp_entity_data.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;

/**
 * Response payload shared between {@code entity_data:inventory} and {@code entity_data:inventory/set}.
 *
 * <p>The {@code inventory} field mirrors the Vanilla NBT {@code Inventory} format exactly —
 * each entry contains {@code Slot}, {@code id}, {@code count}, and optionally {@code components}.</p>
 *
 * <p>Example JSON representation:</p>
 * <pre>{@code
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "inventory": [
 *     { "Slot": 0, "id": "minecraft:diamond_sword", "count": 1, "components": { ... } },
 *     { "Slot": 36, "id": "minecraft:iron_boots", "count": 1 }
 *   ]
 * }
 * }</pre>
 *
 * @param entity    The entity reference; always includes UUID, name only for players
 * @param inventory The occupied inventory slots in Vanilla NBT format
 */
public record InventoryResponse(EntityRef entity, JsonElement inventory) {

    /**
     * Codec for passing {@link JsonElement} through the serialization pipeline without modification.
     */
    public static final Codec<JsonElement> JSON_ELEMENT_CODEC = Codec.PASSTHROUGH.xmap(
        dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(),
        json -> new Dynamic<>(JsonOps.INSTANCE, json)
    );

    /**
     * Codec for serializing and deserializing {@link InventoryResponse} instances.
     */
    public static final Codec<InventoryResponse> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(InventoryResponse::entity),
        JSON_ELEMENT_CODEC.fieldOf("inventory").forGetter(InventoryResponse::inventory)
    ).apply(i, InventoryResponse::new));

    /**
     * MSMP schema for {@link InventoryResponse}, used for protocol discovery.
     */
    public static final Schema<InventoryResponse> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("inventory", Schema.arrayOf(Schema.record(Codec.unit(null)), JSON_ELEMENT_CODEC));
}
