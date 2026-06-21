package dev.loat.msmp_entity.msmp.endpoints.saturation.notification.changed;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.loat.msmp_entity.msmp.components.EntityRef;
import net.minecraft.server.jsonrpc.api.Schema;


/**
 * Payload for the {@code entity:notification/saturation/changed} notification.
 *
 * <p>Fired when a tracked player's food level or saturation has changed. Detection is
 * event-driven (via a mixin on {@code FoodData#tick(Player)}), checked once per server tick.</p>
 *
 * <p>Example JSON representation:</p>
 * <pre><code>
 * {
 *   "entity": { "id": "069a...", "name": "Steve" },
 *   "from_food": 18,
 *   "to_food": 17,
 *   "from_saturation": 5.0,
 *   "to_saturation": 5.0
 * }
 * </code></pre>
 *
 * @param entity The player whose food level or saturation changed
 * @param fromFood The food level at the time of the last notification (or subscription time)
 * @param toFood The current food level
 * @param fromSaturation The saturation level at the time of the last notification (or subscription time)
 * @param toSaturation The current saturation level
 */
public record NotificationSaturationChangedPayload(
    EntityRef entity,
    int fromFood,
    int toFood,
    double fromSaturation,
    double toSaturation
) {

    /** Codec for serializing and deserializing {@link NotificationSaturationChangedPayload} instances. */
    public static final Codec<NotificationSaturationChangedPayload> CODEC = RecordCodecBuilder.create(i -> i.group(
        EntityRef.CODEC.fieldOf("entity").forGetter(NotificationSaturationChangedPayload::entity),
        Codec.INT.fieldOf("from_food").forGetter(NotificationSaturationChangedPayload::fromFood),
        Codec.INT.fieldOf("to_food").forGetter(NotificationSaturationChangedPayload::toFood),
        Codec.DOUBLE.fieldOf("from_saturation").forGetter(NotificationSaturationChangedPayload::fromSaturation),
        Codec.DOUBLE.fieldOf("to_saturation").forGetter(NotificationSaturationChangedPayload::toSaturation)
    ).apply(i, NotificationSaturationChangedPayload::new));

    /** MSMP schema for {@link NotificationSaturationChangedPayload}, used for protocol discovery. */
    public static final Schema<NotificationSaturationChangedPayload> SCHEMA = Schema.record(CODEC)
        .withField("entity", EntityRef.SCHEMA)
        .withField("from_food", Schema.INT_SCHEMA)
        .withField("to_food", Schema.INT_SCHEMA)
        .withField("from_saturation", Schema.NUMBER_SCHEMA)
        .withField("to_saturation", Schema.NUMBER_SCHEMA);
}
