package dev.loat.msmp_entity.mixin;

import dev.loat.msmp_entity.mixin_registry.InventoryChangedMixinRegistry;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Intercepts {@link Inventory#setItem(int, ItemStack)} and {@link Inventory#setChanged()}
 * to detect changes to a player's inventory or equipment.
 *
 * <p>{@code setItem} catches explicit slot writes (pickups, drops, equipment swaps, and the
 * {@code entity:items/set} method). {@code setChanged} catches most in-place mutations of an
 * already-stored {@link ItemStack} (count, durability, or component changes) that never go
 * through {@code setItem}.</p>
 *
 * <p>Both injections only forward the inventory's owning {@code player} to
 * {@link InventoryChangedMixinRegistry}; no diffing happens here. See
 * {@link dev.loat.msmp_entity.msmp.endpoints.items.notification.changed.NotificationItemsChanged}
 * for the actual change detection, which is batched once per tick.</p>
 */
@Mixin(Inventory.class)
public class InventoryChangedMixin {

    @Inject(method = "setItem", at = @At("HEAD"))
    private void onSetItem(int slot, ItemStack stack, CallbackInfo ci) {
        Inventory self = (Inventory) (Object) this;
        InventoryChangedMixinRegistry.invokeCallbacks(self.player);
    }

    @Inject(method = "setChanged", at = @At("HEAD"))
    private void onSetChanged(CallbackInfo ci) {
        Inventory self = (Inventory) (Object) this;
        InventoryChangedMixinRegistry.invokeCallbacks(self.player);
    }
}
