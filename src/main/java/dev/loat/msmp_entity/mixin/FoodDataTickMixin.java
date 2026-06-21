package dev.loat.msmp_entity.mixin;

import dev.loat.msmp_entity.mixin_registry.FoodDataTickMixinRegistry;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Intercepts {@link FoodData#tick(ServerPlayer)} to detect food level and saturation changes.
 *
 * <p>{@code tick()} runs exactly once per player per server tick and is where Vanilla
 * applies exhaustion-driven decay to food/saturation. Eating ({@code FoodData#eat}) mutates
 * the same underlying state directly, outside of {@code tick()} — but since eating always
 * happens earlier in the same server tick (during packet handling, before entities are
 * ticked), the post-tick state observed here already reflects it. Comparing that state
 * against an externally cached "last known" value (see
 * {@link dev.loat.msmp_entity.msmp.endpoints.saturation.notification.changed.NotificationSaturationChanged})
 * reliably catches both sources of change without depending on which internal method
 * actually performed the mutation.</p>
 */
@Mixin(FoodData.class)
public class FoodDataTickMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(ServerPlayer player, CallbackInfo ci) {
        FoodDataTickMixinRegistry.invokeCallbacks(player);
    }
}
