package dev.loat.msmp_entity_data.msmp.methods.position.set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.loat.msmp_entity_data.msmp.components.EntityLookup;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.List;
import java.util.Optional;

/**
 * Request payload for the {@code entity_data:position/set} method.
 *
 * <p>Example JSON representation:</p>
 * <pre>{@code
 * { "name": "Steve", "position": [100.0, 64.0, 200.0] }
 * }</pre>
 *
 * @param id       The entity's UUID as a string, if provided
 * @param name     The player's in-game name, if provided (only works for online players)
 * @param position The target position as {@code [x, y, z]}
 */
public record PositionSetRequest(
    Optional<String> id,
    Optional<String> name,
    List<Double> position
) implements EntityLookup {

    /**
     * Codec for serializing and deserializing {@link PositionSetRequest} instances.
     */
    public static final Codec<PositionSetRequest> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.optionalFieldOf("id").forGetter(PositionSetRequest::id),
        Codec.STRING.optionalFieldOf("name").forGetter(PositionSetRequest::name),
        Codec.DOUBLE.listOf().fieldOf("position").forGetter(PositionSetRequest::position)
    ).apply(i, PositionSetRequest::new));

    /**
     * MSMP schema for {@link PositionSetRequest}, used for protocol discovery.
     */
    public static final Schema<PositionSetRequest> SCHEMA = Schema.record(CODEC)
        .withField("id", Schema.STRING_SCHEMA)
        .withField("name", Schema.STRING_SCHEMA)
        .withField("position", Schema.ofType("array"));
}
