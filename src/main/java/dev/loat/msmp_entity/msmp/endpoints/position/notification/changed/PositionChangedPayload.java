package dev.loat.msmp_entity.msmp.endpoints.position.notification.changed;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.List;

/**
 * Payload for the {@code entity:notification/position/changed} notification.
 *
 * <p>Fired when a subscribed entity has moved at least {@code blockDelta} blocks
 * since the last notification, checked every {@code intervalTicks} server ticks.</p>
 *
 * <p>Example JSON representation:</p>
 * <pre><code>
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "from": [128.5, 64.0, -32.3],
 *   "to": [131.2, 64.0, -29.7]
 * }
 * </code></pre>
 *
 * @param entity The entity that moved
 * @param from The position at the time of the last notification (or subscription time)
 * @param to The current position
 */
public record PositionChangedPayload(EntityRef entity, List<Double> from, List<Double> to) {

    private static final Schema<List<Double>> POSITION_SCHEMA =
        Schema.ofType("array", Codec.DOUBLE.listOf());

    /** Codec for serializing and deserializing {@link PositionChangedPayload} instances. */
    public static final Codec<PositionChangedPayload> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(PositionChangedPayload::entity),
        Codec.DOUBLE.listOf().fieldOf("from").forGetter(PositionChangedPayload::from),
        Codec.DOUBLE.listOf().fieldOf("to").forGetter(PositionChangedPayload::to)
    ).apply(i, PositionChangedPayload::new));

    /** MSMP schema for {@link PositionChangedPayload}, used for protocol discovery. */
    public static final Schema<PositionChangedPayload> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("from", POSITION_SCHEMA)
        .withField("to", POSITION_SCHEMA);
}
