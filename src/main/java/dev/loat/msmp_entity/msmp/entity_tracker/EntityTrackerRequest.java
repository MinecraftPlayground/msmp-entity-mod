package dev.loat.msmp_entity.msmp.entity_tracker;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityRequest;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.List;

/**
 * Request payload for notification subscribe/unsubscribe methods.
 *
 * <p>Provide a list of entity objects to subscribe to.
 * An empty list is a no-op.</p>
 *
 * <p>Example JSON representations:</p>
 * <pre>{@code
 * { "entities": [{ "name": "Steve" }, { "id": "069a79f4-44e9-4726-a5be-fca90e38aaf5" }] }
 * { "entities": [] }  // no-op
 * }</pre>
 *
 * @param entities List of entity lookups to subscribe to
 */
public record EntityTrackerRequest(List<EntityRequest> entities) {

    public static final Codec<EntityTrackerRequest> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRequest.CODEC.listOf().fieldOf("entities").forGetter(EntityTrackerRequest::entities)
    ).apply(i, EntityTrackerRequest::new));

    public static final Schema<EntityTrackerRequest> SCHEMA = Schema.record(CODEC)
        .withField("entities", Schema.ofType("array", EntityRequest.CODEC.listOf()));
}
