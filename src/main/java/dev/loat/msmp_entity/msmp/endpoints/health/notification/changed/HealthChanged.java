package dev.loat.msmp_entity.msmp.endpoints.health.notification.changed;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.config.Config;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.subscription.SubscriptionManager;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;


/**
 * Registers the {@code entity:notification/health/changed} MSMP notification and its
 * tick-based polling loop.
 *
 * <p>On every server tick the counter is incremented. Once it reaches
 * {@code intervalTicks}, every subscribed {@link LivingEntity} is checked.
 * A notification is dispatched only if the entity's health has changed by at least
 * {@code healthDelta} HP since the last recorded value.</p>
 *
 * <p>Setting {@code healthDelta} to {@code 0.0} notifies on any health change.</p>
 *
 * <p>Example notification payload:</p>
 * <pre><code>
 * {
 *   "entity":     { "id": "069a...", "name": "Steve" },
 *   "from":       20.0,
 *   "to":         15.0,
 *   "max_health": 20.0
 * }
 * </code></pre>
 */
public class HealthChanged {

    /** Subscription key used with the global {@link SubscriptionManager}. */
    public static final String SUBSCRIPTION_KEY = "entity:notification/health/changed";

    /**
     * Last recorded health values per entity UUID.
     * Populated on first poll after subscription; updated after each dispatched notification.
     * Entries for unsubscribed entities are left in place — they are effectively inert
     * since the UUID is no longer in the subscription set, and will be overwritten if the
     * entity re-subscribes.
     */
    public static final Map<UUID, Double> LAST_HEALTH = new ConcurrentHashMap<>();

    private HealthChanged() {}

    /**
     * Registers the {@code entity:notification/health/changed} notification and the
     * server-tick polling loop.
     *
     * @param namespace  The namespace to register this notification under
     * @param msmpServer Supplier for the running {@link MSMPServer}; may return {@code null}
     *                   when no server is active
     */
    public static void register(MSMPNamespace namespace, Supplier<MSMPServer> msmpServer) {
        MSMPNotification<HealthChangedPayload> notification = namespace.notification(
            "health/changed",
            HealthChangedPayload.SCHEMA,
            "Fired when a subscribed LivingEntity's health changes by at least healthDelta HP (checked every intervalTicks ticks)"
        );

        SubscriptionManager manager = SubscriptionManager.get(SUBSCRIPTION_KEY);
        int[] tickCounter = {0};

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter[0]++;

            int interval = Config.getConfig().health.notification.intervalTicks;
            if (interval < 1) interval = 1;

            if (tickCounter[0] % interval != 0) return;

            MSMPServer msmp = msmpServer.get();
            if (msmp == null) return;

            double healthDelta = Config.getConfig().health.notification.healthDelta;

            Set<UUID> subscriptions = manager.getSubscriptions();
            if (subscriptions.isEmpty()) return;

            for (UUID uuid : subscriptions) {
                LivingEntity entity = findLivingEntity(server, uuid);
                if (entity == null) continue;

                double currentHealth = entity.getHealth();
                Double lastHealth = LAST_HEALTH.get(uuid);

                if (lastHealth == null) {
                    // First poll after subscription — record health, don't notify yet
                    LAST_HEALTH.put(uuid, currentHealth);
                    continue;
                }

                double diff = Math.abs(currentHealth - lastHealth);
                if (diff < healthDelta) continue;

                double maxHealth = entity.getAttributeValue(Attributes.MAX_HEALTH);

                HealthChangedPayload payload = new HealthChangedPayload(
                    EntityResolver.toEntityRef(entity),
                    lastHealth,
                    currentHealth,
                    maxHealth
                );

                msmp.send(notification, payload);
                LAST_HEALTH.put(uuid, currentHealth);
            }
        });
    }

    /**
     * Searches all loaded levels for the {@link LivingEntity} with the given UUID.
     * Uses the player list as a fast path.
     *
     * @param server The running {@link MinecraftServer}
     * @param uuid   The UUID to look up
     * @return The found {@link LivingEntity}, or {@code null} if not loaded or not a LivingEntity
     */
    private static LivingEntity findLivingEntity(MinecraftServer server, UUID uuid) {
        Entity entity = server.getPlayerList().getPlayer(uuid);
        if (entity instanceof LivingEntity living) return living;

        for (ServerLevel level : server.getAllLevels()) {
            entity = level.getEntity(uuid);
            if (entity instanceof LivingEntity living) return living;
        }
        return null;
    }
}
