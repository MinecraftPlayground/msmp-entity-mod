package dev.loat.msmp_entity.mixin;

import dev.loat.msmp_entity.msmp.endpoints.health.notification.changed.HealthChangedDispatcher;

import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Intercepts {@link LivingEntity#setHealth(float)} to detect health changes
 * for subscribed entities.
 *
 * <p>The injection runs at HEAD, before the new value is applied, so
 * {@code this.getHealth()} still returns the old value. Both old and new
 * values are forwarded to {@link HealthChangedDispatcher#dispatch} which
 * handles subscription checks and sends the MSMP notification.</p>
 */
@Mixin(LivingEntity.class)
public class LivingEntitySetHealthMixin {

    @Inject(method = "setHealth", at = @At("HEAD"))
    private void onSetHealth(float health, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        HealthChangedDispatcher.dispatch(self, self.getHealth(), health);
    }
}
