package dev.loat.msmp_entity.msmp.methods.uuid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.jsonrpc.api.Schema;


/**
 * Request payload for the {@code entity:uuid} method.
 *
 * <p>Example JSON representation:</p>
 * <pre>{@code
 * { "name": "Steve" }
 * }</pre>
 *
 * @param name The player's in-game name
 */
public record UUIDRequest(String name) {

    /**
     * Codec for serializing and deserializing {@link UuidRequest} instances.
     */
    public static final Codec<UUIDRequest> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.fieldOf("name").forGetter(UUIDRequest::name)
    ).apply(i, UUIDRequest::new));

    /**
     * MSMP schema for {@link UuidRequest}, used for protocol discovery.
     */
    public static final Schema<UUIDRequest> SCHEMA = Schema.record(CODEC)
        .withField("name", Schema.STRING_SCHEMA);
}
