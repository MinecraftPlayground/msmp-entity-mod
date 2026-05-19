package dev.loat.msmp_entity_data.msmp.methods.rotation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.loat.msmp_entity_data.msmp.components.EntityLookup;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.List;
import java.util.Optional;


/**
 * Request payload for the {@code entity_data:rotation/set} method.
 *
 * <p>Example JSON representation:</p>
 * <pre>{@code
 * { "name": "Steve", "rotation": [90.0, -15.0] }
 * }</pre>
 *
 * @param id       The entity's UUID as a string, if provided
 * @param name     The player's in-game name, if provided (only works for online players)
 * @param rotation The target rotation as {@code [yaw, pitch]}
 */
public record RotationSetRequest(
    Optional<String> id,
    Optional<String> name,
    List<Double> rotation
) implements EntityLookup {

    /**
     * Codec for serializing and deserializing {@link RotationSetRequest} instances.
     */
    public static final Codec<RotationSetRequest> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.optionalFieldOf("id").forGetter(RotationSetRequest::id),
        Codec.STRING.optionalFieldOf("name").forGetter(RotationSetRequest::name),
        Codec.DOUBLE.listOf().fieldOf("rotation").forGetter(RotationSetRequest::rotation)
    ).apply(i, RotationSetRequest::new));

    /**
     * MSMP schema for {@link RotationSetRequest}, used for protocol discovery.
     */
    public static final Schema<RotationSetRequest> SCHEMA = Schema.record(CODEC)
        .withField("id", Schema.STRING_SCHEMA)
        .withField("name", Schema.STRING_SCHEMA)
        .withField("rotation", Schema.ARRAY_SCHEMA);
}
