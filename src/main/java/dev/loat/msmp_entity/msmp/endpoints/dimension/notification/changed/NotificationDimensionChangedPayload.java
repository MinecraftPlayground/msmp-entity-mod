package dev.loat.msmp_entity.msmp.endpoints.dimension.notification.changed;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;

/**
 * Payload for the {@code entity:notification/dimension/changed} notification.
 *
 * <p>Example JSON representation:</p>
 * <pre><code>
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "from": "minecraft:overworld",
 *   "to": "minecraft:the_nether"
 * }
 * </code></pre>
 *
 * @param entity The entity that changed dimension
 * @param from The dimension the entity came from
 * @param to The dimension the entity entered
 */
public record NotificationDimensionChangedPayload(EntityRef entity, String from, String to) {

    /**
     * Codec for serializing and deserializing {@link NotificationDimensionChangedPayload} instances.
     */
    public static final Codec<NotificationDimensionChangedPayload> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(NotificationDimensionChangedPayload::entity),
        Codec.STRING.fieldOf("from").forGetter(NotificationDimensionChangedPayload::from),
        Codec.STRING.fieldOf("to").forGetter(NotificationDimensionChangedPayload::to)
    ).apply(i, NotificationDimensionChangedPayload::new));

    /**
     * MSMP schema for {@link NotificationDimensionChangedPayload}, used for protocol discovery.
     */
    public static final Schema<NotificationDimensionChangedPayload> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("from", Schema.STRING_SCHEMA)
        .withField("to", Schema.STRING_SCHEMA);
}
