package dev.loat.msmp_entity.mixin_registry;

import net.minecraft.world.entity.LivingEntity;
import java.util.ArrayList;
import java.util.List;

public class LivingEntitySetHealthMixinRegistry {

    @FunctionalInterface
    public interface HealthChangeCallback {
        void onHealthChange(LivingEntity entity, float oldHealth, float newHealth);
    }

    private static final List<HealthChangeCallback> callbacks = new ArrayList<>();

    public static void register(HealthChangeCallback callback) {
        callbacks.add(callback);
    }

    public static void unregister(HealthChangeCallback callback) {
        callbacks.remove(callback);
    }

    public static void invokeCallbacks(LivingEntity entity, float oldHealth, float newHealth) {
        for (HealthChangeCallback callback : callbacks) {
            callback.onHealthChange(entity, oldHealth, newHealth);
        }
    }
}
