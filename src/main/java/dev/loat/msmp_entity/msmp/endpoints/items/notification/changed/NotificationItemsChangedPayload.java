package dev.loat.msmp_entity.msmp.endpoints.items.notification.changed;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;


/**
 * Payload for the {@code entity:notification/items/changed} notification.
 *
 * <p>Fired when a tracked player's inventory or equipment has changed. Detection is
 * event-driven (via mixins on {@code Inventory#setItem}/{@code Inventory#setChanged}),
 * batched once per tick. Contains the full inventory and equipment state at the time
 * the change was detected, in the same Vanilla NBT format as {@code entity:items}.</p>
 *
 * <p>Example JSON representation:</p>
 * <pre><code>
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "inventory": [
 *     { "Slot": 0, "id": "minecraft:diamond_sword", "count": 1 }
 *   ],
 *   "equipment": {
 *     "head": { "id": "minecraft:diamond_helmet", "count": 1 }
 *   }
 * }
 * </code></pre>
 *
 * @param entity The player whose inventory or equipment changed
 * @param inventory The occupied inventory slots in Vanilla NBT format at the time of the change
 * @param equipment The equipped items keyed by slot name (head, chest, legs, feet, offhand)
 */
public record NotificationItemsChangedPayload(EntityRef entity, JsonElement inventory, JsonElement equipment) {

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
     * Codec for serializing and deserializing {@link NotificationItemsChangedPayload} instances.
     */
    public static final Codec<NotificationItemsChangedPayload> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(NotificationItemsChangedPayload::entity),
        JSON_ELEMENT_CODEC.fieldOf("inventory").forGetter(NotificationItemsChangedPayload::inventory),
        JSON_ELEMENT_CODEC.fieldOf("equipment").forGetter(NotificationItemsChangedPayload::equipment)
    ).apply(i, NotificationItemsChangedPayload::new));

    /**
     * MSMP schema for {@link NotificationItemsChangedPayload}, used for protocol discovery.
     */
    public static final Schema<NotificationItemsChangedPayload> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("inventory", INVENTORY_SCHEMA)
        .withField("equipment", EQUIPMENT_SCHEMA);
}
