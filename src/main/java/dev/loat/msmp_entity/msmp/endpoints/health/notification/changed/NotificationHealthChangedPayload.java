package dev.loat.msmp_entity.msmp.endpoints.health.notification.changed;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;


/**
 * Payload for the {@code entity:notification/health/changed} notification.
 *
 * <p>Fired when a subscribed {@link net.minecraft.world.entity.LivingEntity} has gained
 * or lost at least {@code healthDelta} HP since the last notification,
 * checked every {@code intervalTicks} server ticks.</p>
 *
 * <p>Example JSON representation:</p>
 * <pre><code>
 * {
 *   "entity":     { "id": "069a...", "name": "Steve" },
 *   "from":       20.0,
 *   "to":         15.0,
 *   "max_health": 20.0
 * }
 * </code></pre>
 *
 * @param entity    The entity whose health changed
 * @param from      The health at the time of the last notification (or subscription time)
 * @param to        The current health
 * @param maxHealth The entity's current maximum health
 */
public record NotificationHealthChangedPayload(EntityRef entity, double from, double to, double maxHealth) {

    /** Codec for serializing and deserializing {@link NotificationHealthChangedPayload} instances. */
    public static final Codec<NotificationHealthChangedPayload> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(NotificationHealthChangedPayload::entity),
        Codec.DOUBLE.fieldOf("from").forGetter(NotificationHealthChangedPayload::from),
        Codec.DOUBLE.fieldOf("to").forGetter(NotificationHealthChangedPayload::to),
        Codec.DOUBLE.fieldOf("max_health").forGetter(NotificationHealthChangedPayload::maxHealth)
    ).apply(i, NotificationHealthChangedPayload::new));

    /** MSMP schema for {@link NotificationHealthChangedPayload}, used for protocol discovery. */
    public static final Schema<NotificationHealthChangedPayload> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("from", Schema.NUMBER_SCHEMA)
        .withField("to", Schema.NUMBER_SCHEMA)
        .withField("max_health", Schema.NUMBER_SCHEMA);
}
