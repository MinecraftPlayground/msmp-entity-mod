package dev.loat.msmp_entity_data.msmp.methods.position;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.loat.msmp_entity_data.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.List;


/**
 * Response payload shared between {@code entity_data:position} and {@code entity_data:position/set}.
 *
 * <p>Example JSON representation:</p>
 * <pre>{@code
 * {
 *   "entity":   { "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5", "name": "Steve" },
 *   "position": [128.5, 64.0, -32.3]
 * }
 * }</pre>
 *
 * @param entity   The entity reference; always includes UUID, name only for players
 * @param position The entity's position as {@code [x, y, z]}
 */
public record PositionResponse(EntityRef entity, List<Double> position) {

    private static final Schema<List<Double>> POSITION_SCHEMA =
        Schema.ofType("array", Codec.DOUBLE.listOf());

    /**
     * Codec for serializing and deserializing {@link PositionResponse} instances.
     */
    public static final Codec<PositionResponse> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(PositionResponse::entity),
        Codec.DOUBLE.listOf().fieldOf("position").forGetter(PositionResponse::position)
    ).apply(i, PositionResponse::new));

    /**
     * MSMP schema for {@link PositionResponse}, used for protocol discovery.
     */
    public static final Schema<PositionResponse> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("position", POSITION_SCHEMA);
}
