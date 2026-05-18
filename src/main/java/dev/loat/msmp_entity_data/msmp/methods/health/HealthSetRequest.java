package dev.loat.msmp_entity_data.msmp.methods.health;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.loat.msmp_entity_data.msmp.components.EntityLookup;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.Optional;

/**
 * Request payload for the {@code entity_data:health/set} method.
 *
 * <p>At least one of {@code health} or {@code max_health} must be present.
 * Only the provided fields are updated; omitted fields remain unchanged.</p>
 *
 * <p>Example JSON representations:</p>
 * <pre>{@code
 * { "id": "069a...", "health": 15.0 }
 * { "id": "069a...", "max_health": 40.0 }
 * { "name": "Steve", "health": 15.0, "max_health": 40.0 }
 * }</pre>
 *
 * @param id        The entity's UUID as a string, if provided
 * @param name      The player's in-game name, if provided (only works for online players)
 * @param health    The new health value to set, if provided
 * @param maxHealth The new maximum health value to set, if provided
 */
public record HealthSetRequest(
    Optional<String> id,
    Optional<String> name,
    Optional<Double> health,
    Optional<Double> maxHealth
) implements EntityLookup {

    /**
     * Codec for serializing and deserializing {@link HealthSetRequest} instances.
     */
    public static final Codec<HealthSetRequest> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.optionalFieldOf("id").forGetter(HealthSetRequest::id),
        Codec.STRING.optionalFieldOf("name").forGetter(HealthSetRequest::name),
        Codec.DOUBLE.optionalFieldOf("health").forGetter(HealthSetRequest::health),
        Codec.DOUBLE.optionalFieldOf("max_health").forGetter(HealthSetRequest::maxHealth)
    ).apply(i, HealthSetRequest::new));

    /**
     * MSMP schema for {@link HealthSetRequest}, used for protocol discovery.
     */
    public static final Schema<HealthSetRequest> SCHEMA = Schema.record(CODEC)
        .withField("id", Schema.STRING_SCHEMA)
        .withField("name", Schema.STRING_SCHEMA)
        .withField("health", Schema.NUMBER_SCHEMA)
        .withField("max_health", Schema.NUMBER_SCHEMA);
}
