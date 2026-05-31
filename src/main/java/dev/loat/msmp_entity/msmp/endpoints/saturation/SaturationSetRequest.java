package dev.loat.msmp_entity.msmp.endpoints.saturation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityLookup;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.Optional;


/**
 * Request payload for the {@code entity:saturation/set} method.
 *
 * <p>At least one of {@code food} or {@code saturation} must be present.
 * Only the provided fields are updated; omitted fields remain unchanged.</p>
 *
 * <p>Example JSON representations:</p>
 * <pre><code>
 * { "name": "Steve", "food": 20 }
 * { "name": "Steve", "saturation": 10.0 }
 * { "name": "Steve", "food": 20, "saturation": 10.0 }
 * </code></pre>
 *
 * @param id The entity's UUID as a string, if provided
 * @param name The player's in-game name, if provided (only works for online players)
 * @param food The new food level to set (0–20), if provided
 * @param saturation The new saturation level to set (0.0–20.0), if provided
 */
public record SaturationSetRequest(
    Optional<String> id,
    Optional<String> name,
    Optional<Integer> food,
    Optional<Double> saturation
) implements EntityLookup {

    /**
     * Codec for serializing and deserializing {@link SaturationSetRequest} instances.
     */
    public static final Codec<SaturationSetRequest> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.optionalFieldOf("id").forGetter(SaturationSetRequest::id),
        Codec.STRING.optionalFieldOf("name").forGetter(SaturationSetRequest::name),
        Codec.INT.optionalFieldOf("food").forGetter(SaturationSetRequest::food),
        Codec.DOUBLE.optionalFieldOf("saturation").forGetter(SaturationSetRequest::saturation)
    ).apply(i, SaturationSetRequest::new));

    /**
     * MSMP schema for {@link SaturationSetRequest}, used for protocol discovery.
     */
    public static final Schema<SaturationSetRequest> SCHEMA = Schema.record(CODEC)
        .withField("id", Schema.STRING_SCHEMA)
        .withField("name", Schema.STRING_SCHEMA)
        .withField("food", Schema.INT_SCHEMA)
        .withField("saturation", Schema.NUMBER_SCHEMA);
}
