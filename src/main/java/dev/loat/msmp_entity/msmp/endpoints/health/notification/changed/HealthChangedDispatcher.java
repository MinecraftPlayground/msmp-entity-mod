package dev.loat.msmp_entity.msmp.endpoints.health.notification.changed;

import dev.loat.msmp.MSMPServer;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.subscription.SubscriptionManager;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.function.Supplier;


/**
 * Static dispatcher called by {@link dev.loat.msmp_entity.mixin.LivingEntitySetHealthMixin}
 * whenever {@code LivingEntity#setHealth(float)} is invoked.
 *
 * <p>Decouples the Mixin (which cannot hold instance state) from the notification
 * infrastructure. Initialized once in {@link HealthChanged#register}.</p>
 */
public final class HealthChangedDispatcher {

    private static MSMPNotification<HealthChangedPayload> notification;
    private static Supplier<MSMPServer> msmpServer;

    private HealthChangedDispatcher() {}

    /**
     * Called by {@link HealthChanged#register} to wire up the notification and server supplier.
     *
     * @param notification The registered {@link MSMPNotification} to send
     * @param msmpServer   Supplier for the running {@link MSMPServer}
     */
    static void init(
        MSMPNotification<HealthChangedPayload> notification,
        Supplier<MSMPServer> msmpServer
    ) {
        HealthChangedDispatcher.notification = notification;
        HealthChangedDispatcher.msmpServer = msmpServer;
    }

    /**
     * Dispatches a health change notification if the entity is subscribed and health
     * actually changed.
     *
     * <p>Called from the Mixin at the HEAD of {@code setHealth(float)}, so
     * {@code oldHealth} is read before the new value is applied.</p>
     *
     * @param entity    The entity whose health is about to change
     * @param oldHealth The current health before the change
     * @param newHealth The incoming health value
     */
    public static void dispatch(LivingEntity entity, float oldHealth, float newHealth) {
        if (notification == null || msmpServer == null) return;
        if (oldHealth == newHealth) return;

        if (!SubscriptionManager.get(HealthChanged.SUBSCRIPTION_KEY).isSubscribed(entity.getUUID())) return;

        MSMPServer server = msmpServer.get();
        if (server == null) return;

        double maxHealth = entity.getAttributeValue(Attributes.MAX_HEALTH);

        server.send(notification, new HealthChangedPayload(
            EntityResolver.toEntityRef(entity),
            oldHealth,
            newHealth,
            maxHealth
        ));
    }
}
