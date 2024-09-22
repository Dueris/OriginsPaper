package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.power.type.ActionOnItemPickupPower;
import io.github.dueris.originspaper.power.type.PreventItemPickupPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

	@WrapOperation(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean apoli$onItemPickup(Inventory instance, ItemStack stack, Operation<Boolean> original, Player player) {

		ItemEntity thisAsItemEntity = (ItemEntity) (Object) this;
		if (PreventItemPickupPower.doesPrevent(thisAsItemEntity, player)) {
			return false;
		}

		if (original.call(instance, stack)) {
			for (ActionOnItemPickupPower power : PowerHolderComponent.getPowers(player.getBukkitEntity(), ActionOnItemPickupPower.class)) {
				if (power.doesApply(thisAsItemEntity.getItem(), player)) {
					power.executeActions(thisAsItemEntity.getItem(), player);
				}
			}
			return true;
		} else {
			return false;
		}

	}
}
