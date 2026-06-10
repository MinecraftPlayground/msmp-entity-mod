package dev.loat.msmp_entity.msmp.entity_tracker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.List;

/**
 * Common response payload for notification add and remove methods.
 *
 * <p>Returns the list of entities now being tracked/untracked.</p>
 *
 * <p>Example JSON representations:</p>
 * <pre><code>
 *
 * // Specific entity added/removed:
 * { "add": [{ "id": "069a...", "name": "Steve" }] }
 * </code></pre>
 *
 * @param entities The entities now tracked/untracked
 */
public record EntityTrackerResponse(List<EntityRef> entities) {

    /**
     * Codec for serializing and deserializing {@link EntityTrackerResponse} instances.
     */
    public static final Codec<EntityTrackerResponse> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.listOf().fieldOf("entities").forGetter(EntityTrackerResponse::entities)
    ).apply(i, EntityTrackerResponse::new));

    /**
     * MSMP schema for {@link EntityTrackerResponse}, used for protocol discovery.
     */
    public static final Schema<EntityTrackerResponse> SCHEMA = Schema.record(CODEC)
        .withField("entities", Schema.ofType("array", EntityRef.CODEC.listOf()));
}
