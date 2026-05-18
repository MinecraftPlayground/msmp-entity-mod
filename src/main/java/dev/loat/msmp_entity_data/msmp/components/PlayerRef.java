package dev.loat.msmp_entity_data.msmp.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.jsonrpc.api.Schema;


/**
 * Represents an player reference used in responses.
 * <p>{@code id} (UUID) and {@code name} are always present for players.</p>
 *
 * <p>Example JSON representations:</p>
 * <pre>{@code
 * { "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5", "name": "Steve" }
 * }</pre>
 *
 * @param id The entity's UUID as a string, always present
 * @param name The player's in-game name, only present if the entity is a player
 */
public record PlayerRef(String id, String name) {

    /**
     * Codec for serializing and deserializing {@link PlayerRef} instances.
     */
    public static final Codec<PlayerRef> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.fieldOf("id").forGetter(PlayerRef::id),
        Codec.STRING.fieldOf("name").forGetter(PlayerRef::name)
    ).apply(i, PlayerRef::new));

    /**
     * MSMP schema for {@link PlayerRef}, used for protocol discovery.
     */
    public static final Schema<PlayerRef> SCHEMA = Schema.record(CODEC)
        .withField("id", Schema.STRING_SCHEMA)
        .withField("name", Schema.STRING_SCHEMA);
}
