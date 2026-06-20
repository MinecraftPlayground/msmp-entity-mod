package dev.loat.msmp_entity.msmp.endpoints.rotation.notification.changed;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;

import java.util.List;

/**
 * Payload for the {@code entity:notification/rotation/changed} notification.
 *
 * <p>Fired when a subscribed entity has rotated at least {@code rotationDelta} degrees
 * (in yaw or pitch) since the last notification, checked every {@code intervalTicks}
 * server ticks.</p>
 *
 * <p>Example JSON representation:</p>
 * <pre><code>
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "from": [90.0, -15.0],
 *   "to": [125.0, 10.0]
 * }
 * </code></pre>
 *
 * @param entity The entity that rotated
 * @param from The rotation at the time of the last notification (or subscription time) as {@code [yaw, pitch]}
 * @param to The current rotation as {@code [yaw, pitch]}
 */
public record NotificationRotationChangedPayload(EntityRef entity, List<Double> from, List<Double> to) {

    private static final Schema<List<Double>> ROTATION_SCHEMA =
        Schema.ofType("array", Codec.DOUBLE.listOf());

    /** Codec for serializing and deserializing {@link NotificationRotationChangedPayload} instances. */
    public static final Codec<NotificationRotationChangedPayload> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(NotificationRotationChangedPayload::entity),
        Codec.DOUBLE.listOf().fieldOf("from").forGetter(NotificationRotationChangedPayload::from),
        Codec.DOUBLE.listOf().fieldOf("to").forGetter(NotificationRotationChangedPayload::to)
    ).apply(i, NotificationRotationChangedPayload::new));

    /** MSMP schema for {@link NotificationRotationChangedPayload}, used for protocol discovery. */
    public static final Schema<NotificationRotationChangedPayload> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("from", ROTATION_SCHEMA)
        .withField("to", ROTATION_SCHEMA);
}
