package dev.loat.msmp_entity.mixin_registry;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;


/**
 * Registry of callbacks notified whenever a player's {@code Inventory} is mutated.
 *
 * <p>Fed by {@link dev.loat.msmp_entity.mixin.InventoryChangedMixin}. Callbacks are
 * invoked synchronously on the calling thread (normally the server thread) and should
 * stay cheap — heavy work such as serialization or diffing should be deferred. See
 * {@link dev.loat.msmp_entity.msmp.endpoints.items.notification.changed.NotificationItemsChanged}
 * for how this is used: the callback there only flags a UUID as dirty, and the actual
 * diff/serialize/send work happens once per tick.</p>
 */
public class InventoryChangedMixinRegistry {

    @FunctionalInterface
    public interface InventoryChangeCallback {
        void onInventoryChange(Player player);
    }

    private static final List<InventoryChangeCallback> callbacks = new ArrayList<>();

    public static void register(InventoryChangeCallback callback) {
        callbacks.add(callback);
    }

    public static void unregister(InventoryChangeCallback callback) {
        callbacks.remove(callback);
    }

    public static void invokeCallbacks(Player player) {
        if (player == null) return;
        for (InventoryChangeCallback callback : callbacks) {
            callback.onInventoryChange(player);
        }
    }
}
