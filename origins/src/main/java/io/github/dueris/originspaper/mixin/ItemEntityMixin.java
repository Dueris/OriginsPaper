package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.power.type.ActionOnItemPickupPowerType;
import io.github.dueris.originspaper.power.type.PreventItemPickupPowerType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

	public ItemEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@WrapOperation(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean apoli$onItemPickup(Inventory instance, ItemStack stack, Operation<Boolean> original, Player player) {

		ItemEntity thisAsItemEntity = (ItemEntity) (Object) this;
		if (PreventItemPickupPowerType.doesPrevent(thisAsItemEntity, player)) {
			return false;
		}

		if (original.call(instance, stack)) {
			ActionOnItemPickupPowerType.executeActions(thisAsItemEntity, player);
			return true;
		} else {
			return false;
		}

	}

}
