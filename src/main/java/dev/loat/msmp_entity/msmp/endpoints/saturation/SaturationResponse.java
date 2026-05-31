package dev.loat.msmp_entity.msmp.endpoints.saturation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityRef;

import net.minecraft.server.jsonrpc.api.Schema;


/**
 * Response payload shared between {@code entity:saturation} and {@code entity:saturation/set}.
 *
 * <p>Example JSON representation:</p>
 * <pre><code>
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "food": 18,
 *   "saturation": 5.0
 * }
 * }</pre>
 *
 * @param entity The entity reference; always includes UUID and name (players only)
 * @param food The player's current food level (0–20)
 * @param saturation The player's current saturation level (0.0–20.0)
 */
public record SaturationResponse(EntityRef entity, int food, double saturation) {

    /**
     * Codec for serializing and deserializing {@link SaturationResponse} instances.
     */
    public static final Codec<SaturationResponse> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(SaturationResponse::entity),
        Codec.INT.fieldOf("food").forGetter(SaturationResponse::food),
        Codec.DOUBLE.fieldOf("saturation").forGetter(SaturationResponse::saturation)
    ).apply(i, SaturationResponse::new));

    /**
     * MSMP schema for {@link SaturationResponse}, used for protocol discovery.
     */
    public static final Schema<SaturationResponse> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("food", Schema.INT_SCHEMA)
        .withField("saturation", Schema.NUMBER_SCHEMA);
}
