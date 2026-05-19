package dev.loat.msmp_entity_data.msmp.methods.inventory.set;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.loat.msmp_entity_data.msmp.components.EntityLookup;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.Optional;


/**
 * Request payload for the {@code entity_data:inventory/set} method.
 *
 * <p>Only the provided slots are updated; all other slots remain unchanged.
 * To clear a slot, provide {@code "id": "minecraft:air"}.</p>
 *
 * <p>Example JSON representation:</p>
 * <pre>{@code
 * {
 *   "name": "Steve",
 *   "inventory": [
 *     { "Slot": 0, "id": "minecraft:diamond_sword", "count": 1 },
 *     { "Slot": 9, "id": "minecraft:air", "count": 0 }
 *   ]
 * }
 * }</pre>
 *
 * @param id        The entity's UUID as a string, if provided
 * @param name      The player's in-game name, if provided (only works for online players)
 * @param inventory The list of slot updates in Vanilla NBT format
 */
public record InventorySetRequest(
    Optional<String> id,
    Optional<String> name,
    JsonElement inventory
) implements EntityLookup {

    /**
     * Codec for passing {@link JsonElement} through the serialization pipeline without modification.
     */
    public static final Codec<JsonElement> JSON_ELEMENT_CODEC = Codec.PASSTHROUGH.xmap(
        dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(),
        json -> new Dynamic<>(JsonOps.INSTANCE, json)
    );

    /**
     * Codec for serializing and deserializing {@link InventorySetRequest} instances.
     */
    public static final Codec<InventorySetRequest> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.optionalFieldOf("id").forGetter(InventorySetRequest::id),
        Codec.STRING.optionalFieldOf("name").forGetter(InventorySetRequest::name),
        JSON_ELEMENT_CODEC.fieldOf("inventory").forGetter(InventorySetRequest::inventory)
    ).apply(i, InventorySetRequest::new));

    /**
     * MSMP schema for {@link InventorySetRequest}, used for protocol discovery.
     */
    public static final Schema<InventorySetRequest> SCHEMA = Schema.record(CODEC)
        .withField("id", Schema.STRING_SCHEMA)
        .withField("name", Schema.STRING_SCHEMA)
        .withField("inventory", Schema.arrayOf(Schema.record(Codec.unit(null)), JSON_ELEMENT_CODEC));
}
