package dev.loat.msmp_entity_data.msmp.subscription;


/**
 * Enum of all subscribable entity data events.
 *
 * <p>Used by clients to specify which events they want to receive
 * notifications for via {@code entity_data:subscribe}.</p>
 */
public enum SubscriptionEvent {

    /** Fired when a tracked entity changes dimension. */
    DIMENSION_CHANGED,

    /** Fired when a tracked entity's health changes. */
    HEALTH_CHANGED,

    /** Fired when a tracked player dies. */
    DEATH,

    /** Fired when a tracked player respawns. */
    RESPAWN;

    /**
     * Parses a {@link SubscriptionEvent} from its lowercase string representation.
     *
     * @param value The string value (e.g. {@code "dimension_changed"})
     * @return The matching {@link SubscriptionEvent}
     * @throws IllegalArgumentException if the value does not match any event
     */
    public static SubscriptionEvent fromString(String value) {
        return valueOf(value.toUpperCase());
    }

    /**
     * Returns the lowercase string representation of this event,
     * as used in the MSMP protocol.
     *
     * @return The lowercase event name (e.g. {@code "dimension_changed"})
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
