package dev.loat.msmp_entity_data.msmp.subscription;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.loat.msmp_entity_data.msmp.components.EntityLookup;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.Optional;

/**
 * Common request payload for notification subscribe methods.
 *
 * <p>At least one of {@code id} or {@code name} must be present to subscribe
 * to a specific entity. If both are omitted, a wildcard subscription is created
 * (all entities).</p>
 *
 * <p>Example JSON representations:</p>
 * <pre>{@code
 * {}                                                    // wildcard — all entities
 * { "name": "Steve" }                                   // specific player by name
 * { "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5" }    // specific entity by UUID
 * }</pre>
 *
 * @param id   The entity's UUID as a string, if provided
 * @param name The player's in-game name, if provided
 */
public record SubscribeRequest(Optional<String> id, Optional<String> name) implements EntityLookup {

    /**
     * Codec for serializing and deserializing {@link SubscribeRequest} instances.
     */
    public static final Codec<SubscribeRequest> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.optionalFieldOf("id").forGetter(SubscribeRequest::id),
        Codec.STRING.optionalFieldOf("name").forGetter(SubscribeRequest::name)
    ).apply(i, SubscribeRequest::new));

    /**
     * MSMP schema for {@link SubscribeRequest}, used for protocol discovery.
     */
    public static final Schema<SubscribeRequest> SCHEMA = Schema.record(CODEC)
        .withField("id", Schema.STRING_SCHEMA)
        .withField("name", Schema.STRING_SCHEMA);

    /**
     * Returns {@code true} if this request is a wildcard subscription
     * (neither {@code id} nor {@code name} provided).
     *
     * @return {@code true} if wildcard
     */
    public boolean isWildcard() {
        return id().isEmpty() && name().isEmpty();
    }
}
