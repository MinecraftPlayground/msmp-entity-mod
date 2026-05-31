package dev.loat.msmp_entity.msmp.endpoints.items;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityLookup;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.Optional;


/**
 * Request payload for the {@code entity:inventory/set} method.
 *
 * <p>Only the provided slots are updated; all other slots remain unchanged.
 * To clear a slot, provide {@code "id": "minecraft:air"}.</p>
 *
 * <p>Example JSON representation:</p>
 * <pre><code>
 * {
 *   "name": "Steve",
 *   "inventory": [
 *     { "Slot": 0, "id": "minecraft:diamond_sword", "count": 1 },
 *     { "Slot": 9, "id": "minecraft:air", "count": 0 }
 *   ],
 *   "equipment": {
 *     "head": { "id": "minecraft:diamond_helmet", "count": 1 }
 *   }
 * }
 * </code></pre>
 *
 * @param id The entity's UUID as a string, if provided
 * @param name The player's in-game name, if provided (only works for online players)
 * @param inventory The list of slot updates in Vanilla NBT format
 * @param equipment The equipment updates keyed by slot name (head, chest, legs, feet, offhand)
 */
public record ItemsSetRequest(
    Optional<String> id,
    Optional<String> name,
    JsonElement inventory,
    JsonElement equipment
) implements EntityLookup {

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
     * Codec for serializing and deserializing {@link ItemsSetRequest} instances.
     */
    public static final Codec<ItemsSetRequest> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.optionalFieldOf("id").forGetter(ItemsSetRequest::id),
        Codec.STRING.optionalFieldOf("name").forGetter(ItemsSetRequest::name),
        JSON_ELEMENT_CODEC.fieldOf("inventory").forGetter(ItemsSetRequest::inventory),
        JSON_ELEMENT_CODEC.fieldOf("equipment").forGetter(ItemsSetRequest::equipment)
    ).apply(i, ItemsSetRequest::new));

    /**
     * MSMP schema for {@link ItemsSetRequest}, used for protocol discovery.
     */
    public static final Schema<ItemsSetRequest> SCHEMA = Schema.record(CODEC)
        .withField("id", Schema.STRING_SCHEMA)
        .withField("name", Schema.STRING_SCHEMA)
        .withField("inventory", INVENTORY_SCHEMA)
        .withField("equipment", EQUIPMENT_SCHEMA);
}
