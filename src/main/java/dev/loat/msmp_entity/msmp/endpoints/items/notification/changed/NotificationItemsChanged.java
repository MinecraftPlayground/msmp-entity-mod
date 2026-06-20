package dev.loat.msmp_entity.msmp.endpoints.items.notification.changed;

import com.google.gson.JsonObject;

import dev.loat.msmp.MSMPNamespace;
import dev.loat.msmp.MSMPNotification;
import dev.loat.msmp.MSMPServer;
import dev.loat.msmp_entity.mixin_registry.InventoryChangedMixinRegistry;
import dev.loat.msmp_entity.msmp.components.EntityResolver;
import dev.loat.msmp_entity.msmp.endpoints.items.ItemsSnapshot;
import dev.loat.msmp_entity.msmp.entity_tracker.EntityTracker;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;


/**
 * Registers the {@code entity:notification/items/changed} MSMP notification.
 *
 * <p>Detection is fully event-driven via {@link InventoryChangedMixinRegistry}, fed by
 * mixins on {@code Inventory#setItem} and {@code Inventory#setChanged}. The mixin callback
 * only flags the player's UUID as dirty — the actual snapshot diff and notification dispatch
 * happens once per tick, so several change calls within the same tick (e.g. shift-clicking
 * many slots into a chest) only trigger a single diff/serialize/send cycle instead of one
 * per call.</p>
 */
public class NotificationItemsChanged {

    public static final String TRACKER_KEY = "entity:notification/items/changed";

    /**
     * Last recorded inventory/equipment snapshot per player UUID.
     * Established synchronously on add (see {@code ItemsChangedAdd}); cleared on remove.
     */
    public static final Map<UUID, JsonObject> LAST_ITEMS = new ConcurrentHashMap<>();

    /**
     * UUIDs of tracked players whose inventory was touched since the last tick check.
     * Populated by the mixin callback, drained once per tick.
     */
    private static final Set<UUID> DIRTY = ConcurrentHashMap.newKeySet();

    private NotificationItemsChanged() {}

    public static void register(MSMPNamespace namespace, Supplier<MSMPServer> msmpServer) {

        MSMPNotification<NotificationItemsChangedPayload> notification = namespace.notification("items/changed")
            .description("Fired when a tracked player's inventory or equipment changes")
            .responseSchema(NotificationItemsChangedPayload.SCHEMA)
            .register();

        EntityTracker tracker = EntityTracker.get(TRACKER_KEY);

        InventoryChangedMixinRegistry.register(player -> {
            if (!tracker.contains(player.getUUID())) return;
            DIRTY.add(player.getUUID());
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (DIRTY.isEmpty()) return;

            MSMPServer msmp = msmpServer.get();
            if (msmp == null) return;

            List<UUID> pending = new ArrayList<>(DIRTY);
            DIRTY.removeAll(pending);

            for (UUID uuid : pending) {
                if (!tracker.contains(uuid)) continue;

                Player player = server.getPlayerList().getPlayer(uuid);
                if (player == null) continue;

                JsonObject current = ItemsSnapshot.of(server, player);
                JsonObject last = LAST_ITEMS.get(uuid);

                if (last != null && last.equals(current)) continue;

                LAST_ITEMS.put(uuid, current);

                // No baseline yet — shouldn't normally happen, since ItemsChangedAdd
                // establishes one synchronously. Store it without firing, just like
                // entity:notification/position/changed does on its first poll.
                if (last == null) continue;

                msmp.send(notification, new NotificationItemsChangedPayload(
                    EntityResolver.toEntityRef(player),
                    current.get("inventory"),
                    current.get("equipment")
                ));
            }
        });
    }

    /**
     * Clears all cached state for the given player UUID.
     * Called by {@code entity:items/changed/remove} when untracking a player.
     *
     * @param uuid The UUID to forget
     */
    public static void forget(UUID uuid) {
        LAST_ITEMS.remove(uuid);
        DIRTY.remove(uuid);
    }
}
