package dev.loat.msmp_entity_data.msmp.methods.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.loat.msmp_entity_data.msmp.components.EntityLookup;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.Optional;


/**
 * Request payload for the {@code entity_data:dimension/set} method.
 *
 * <p>Transfers the entity to the given dimension at its current position.</p>
 *
 * <p>Example JSON representation:</p>
 * <pre>{@code
 * { "name": "Steve", "dimension": "minecraft:the_nether" }
 * }</pre>
 *
 * @param id        The entity's UUID as a string, if provided
 * @param name      The player's in-game name, if provided (only works for online players)
 * @param dimension The target dimension resource key (e.g. {@code minecraft:the_nether})
 */
public record DimensionSetRequest(
    Optional<String> id,
    Optional<String> name,
    String dimension
) implements EntityLookup {

    /**
     * Codec for serializing and deserializing {@link DimensionSetRequest} instances.
     */
    public static final Codec<DimensionSetRequest> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.optionalFieldOf("id").forGetter(DimensionSetRequest::id),
        Codec.STRING.optionalFieldOf("name").forGetter(DimensionSetRequest::name),
        Codec.STRING.fieldOf("dimension").forGetter(DimensionSetRequest::dimension)
    ).apply(i, DimensionSetRequest::new));

    /**
     * MSMP schema for {@link DimensionSetRequest}, used for protocol discovery.
     */
    public static final Schema<DimensionSetRequest> SCHEMA = Schema.record(CODEC)
        .withField("id", Schema.STRING_SCHEMA)
        .withField("name", Schema.STRING_SCHEMA)
        .withField("dimension", Schema.STRING_SCHEMA);
}
