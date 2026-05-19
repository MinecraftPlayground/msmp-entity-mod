package dev.loat.msmp_entity_data.msmp.methods.rotation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.loat.msmp_entity_data.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.List;


/**
 * Response payload shared between {@code entity_data:rotation} and {@code entity_data:rotation/set}.
 *
 * <p>Example JSON representation:</p>
 * <pre>{@code
 * {
 *   "entity":   { "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5", "name": "Steve" },
 *   "rotation": [90.0, -15.0]
 * }
 * }</pre>
 *
 * @param entity   The entity reference; always includes UUID, name only for players
 * @param rotation The entity's rotation as {@code [yaw, pitch]}
 */
public record RotationResponse(EntityRef entity, List<Double> rotation) {

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
        .withField("rotation", Schema.ARRAY_SCHEMA);
}
