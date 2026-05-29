package dev.loat.msmp_entity.msmp.subscription;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.List;

/**
 * Common response payload for notification subscribe and unsubscribe methods.
 *
 * <p>Returns the list of entities now being tracked. An empty list means
 * the connection is subscribed with a wildcard (all entities).</p>
 *
 * <p>Example JSON representations:</p>
 * <pre>{@code
 * // Wildcard subscription:
 * { "subscribed": [] }
 *
 * // Specific entity subscription:
 * { "subscribed": [{ "id": "069a...", "name": "Steve" }] }
 * }</pre>
 *
 * @param subscribed The entities now tracked; empty means wildcard
 */
public record SubscribeResponse(List<EntityRef> subscribed) {

    /**
     * Codec for serializing and deserializing {@link SubscribeResponse} instances.
     */
    public static final Codec<SubscribeResponse> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.listOf().fieldOf("subscribed").forGetter(SubscribeResponse::subscribed)
    ).apply(i, SubscribeResponse::new));

    /**
     * MSMP schema for {@link SubscribeResponse}, used for protocol discovery.
     */
    public static final Schema<SubscribeResponse> SCHEMA = Schema.record(CODEC)
        .withField("subscribed", Schema.ofType("array", EntityRef.CODEC.listOf()));
}
