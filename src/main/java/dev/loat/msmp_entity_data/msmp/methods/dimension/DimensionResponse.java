package dev.loat.msmp_entity_data.msmp.methods.dimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.loat.msmp_entity_data.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;

/**
 * Response payload shared between {@code entity_data:dimension} and {@code entity_data:dimension/set}.
 *
 * <p>Example JSON representation:</p>
 * <pre>{@code
 * {
 *   "entity":    { "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5", "name": "Steve" },
 *   "dimension": "minecraft:overworld"
 * }
 * }</pre>
 *
 * @param entity    The entity reference; always includes UUID, name only for players
 * @param dimension The resource key of the dimension the entity is currently in
 */
public record DimensionResponse(EntityRef entity, String dimension) {

    /**
     * Codec for serializing and deserializing {@link DimensionResponse} instances.
     */
    public static final Codec<DimensionResponse> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(DimensionResponse::entity),
        Codec.STRING.fieldOf("dimension").forGetter(DimensionResponse::dimension)
    ).apply(i, DimensionResponse::new));

    /**
     * MSMP schema for {@link DimensionResponse}, used for protocol discovery.
     */
    public static final Schema<DimensionResponse> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("dimension", Schema.STRING_SCHEMA);
}
