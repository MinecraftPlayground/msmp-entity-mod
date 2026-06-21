package dev.loat.msmp_entity.mixin_registry;

import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;


/**
 * Registry of callbacks notified once per player per server tick, right after
 * {@code FoodData#tick(Player)} has applied Vanilla's exhaustion-driven decay
 * (and reflects any earlier eating that happened this same tick).
 *
 * <p>Fed by {@link dev.loat.msmp_entity.mixin.FoodDataTickMixin}. Callbacks are invoked
 * synchronously on the calling thread (normally the server thread) for every player,
 * tracked or not — the registered callback is expected to cheaply filter by tracking
 * membership before doing any heavier work. See
 * {@link dev.loat.msmp_entity.msmp.endpoints.saturation.notification.changed.NotificationSaturationChanged}.</p>
 */
public class FoodDataTickMixinRegistry {

    @FunctionalInterface
    public interface FoodDataTickCallback {
        void onFoodDataTick(Player player);
    }

    private static final List<FoodDataTickCallback> callbacks = new ArrayList<>();

    public static void register(FoodDataTickCallback callback) {
        callbacks.add(callback);
    }

    public static void unregister(FoodDataTickCallback callback) {
        callbacks.remove(callback);
    }

    public static void invokeCallbacks(Player player) {
        if (player == null) return;
        for (FoodDataTickCallback callback : callbacks) {
            callback.onFoodDataTick(player);
        }
    }
}
