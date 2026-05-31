package dev.loat.msmp_entity.msmp.endpoints.rotation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.List;


/**
 * Response payload for {@code entity:rotation} and {@code entity:rotation/set}.
 *
 * <p>Example JSON representation:</p>
 * <pre><code>
 * {
 *   "entity":   { "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5", "name": "Steve" },
 *   "rotation": [90.0, -15.0]
 * }
 * </code></pre>
 *
 * @param entity The entity reference; always includes UUID, name only for players
 * @param rotation The entity's rotation as {@code [yaw, pitch]}
 */
public record RotationResponse(EntityRef entity, List<Double> rotation) {

    private static final Schema<List<Double>> ROTATION_SCHEMA =
        Schema.ofType("array", Codec.DOUBLE.listOf());

    /**
     * Codec for serializing and deserializing {@link RotationResponse} instances.
     */
    public static final Codec<RotationResponse> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(RotationResponse::entity),
        Codec.DOUBLE.listOf().fieldOf("rotation").forGetter(RotationResponse::rotation)
    ).apply(i, RotationResponse::new));

    /**
     * MSMP schema for {@link RotationResponse}, used for protocol discovery.
     */
    public static final Schema<RotationResponse> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("rotation", ROTATION_SCHEMA);
}
