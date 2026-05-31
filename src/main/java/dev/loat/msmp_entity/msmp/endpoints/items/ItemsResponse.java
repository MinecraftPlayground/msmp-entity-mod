package dev.loat.msmp_entity.msmp.endpoints.items;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;


/**
 * Response payload shared between {@code entity:items} and {@code entity:items/set}.
 *
 * <p>The {@code inventory} field mirrors the Vanilla NBT {@code Inventory} format exactly —
 * each entry contains {@code Slot}, {@code id}, {@code count}, and optionally {@code components}.</p>
 *
 * <p>The {@code equipment} field is an object with keys for each equipped item slot:
 * {@code head}, {@code chest}, {@code legs}, {@code feet}, {@code offhand}.</p>
 *
 * <p>Example JSON representation:</p>
 * <pre>{@code
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "inventory": [
 *     { "Slot": 0, "id": "minecraft:diamond_sword", "count": 1, "components": { ... } },
 *     { "Slot": 20, "id": "minecraft:iron_boots", "count": 1 }
 *   ],
 *   "equipment": {
 *     "head": { "id": "minecraft:diamond_helmet", "count": 1 },
 *     "feet": { "id": "minecraft:diamond_boots", "count": 1 }
 *   }
 * }
 * }</pre>
 *
 * @param entity The entity reference; always includes UUID, name only for players
 * @param inventory The occupied inventory slots in Vanilla NBT format
 * @param equipment The equipped items keyed by slot name (head, chest, legs, feet, offhand)
 */
public record ItemsResponse(EntityRef entity, JsonElement inventory, JsonElement equipment) {

    /**
     * Codec for passing {@link JsonElement} through the serialization pipeline without modification.
     */
    public static final Codec<JsonElement> JSON_ELEMENT_CODEC = Codec.PASSTHROUGH.xmap(
        dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(),
        json -> new Dynamic<>(JsonOps.INSTANCE, json)
    );

    private static final Schema<JsonElement> INVENTORY_SCHEMA =
        Schema.ofType("array", JSON_ELEMENT_CODEC);

    private static final Schema<JsonElement> EQUIPMENT_SCHEMA =
        Schema.ofType("object", JSON_ELEMENT_CODEC);

    /**
     * Codec for serializing and deserializing {@link ItemsResponse} instances.
     */
    public static final Codec<ItemsResponse> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(ItemsResponse::entity),
        JSON_ELEMENT_CODEC.fieldOf("inventory").forGetter(ItemsResponse::inventory),
        JSON_ELEMENT_CODEC.fieldOf("equipment").forGetter(ItemsResponse::equipment)
    ).apply(i, ItemsResponse::new));

    /**
     * MSMP schema for {@link ItemsResponse}, used for protocol discovery.
     */
    public static final Schema<ItemsResponse> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("inventory", INVENTORY_SCHEMA)
        .withField("equipment", EQUIPMENT_SCHEMA);
}
