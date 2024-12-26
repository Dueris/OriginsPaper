package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.power.type.ActionOnItemPickupPowerType;
import io.github.dueris.originspaper.power.type.PreventItemPickupPowerType;
import io.github.dueris.originspaper.power.type.Prioritized;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

	@Shadow @Nullable public UUID thrower;

	@Shadow public abstract void setItem(ItemStack stack);

	@Shadow public abstract ItemStack getItem();

	public ItemEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@WrapOperation(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean apoli$onItemPickup(Inventory playerInventory, ItemStack stack, Operation<Boolean> original, Player player) {

		if (PreventItemPickupPowerType.doesPrevent(originspaper$thisAsItemEntity(), player)) {
			return false;
		}

		else if (Util.hasSpaceInInventory(playerInventory, stack)) {

			SlotAccess stackReference = InventoryUtil.createStackReference(stack);
			Entity thrower = Util.getEntityByUuid(this.thrower, this.getServer());

			Prioritized.CallInstance<ActionOnItemPickupPowerType> callInstance = ActionOnItemPickupPowerType.executeItemAction(thrower, stackReference, player);
			this.setItem(stackReference.get());

			boolean result = original.call(playerInventory, this.getItem());
			if (result) {
				ActionOnItemPickupPowerType.executeBiEntityAction(callInstance, thrower);
			}

			return result;
		} else {
			return original.call(playerInventory, stack);
		}

	}

	@Unique
	private ItemEntity originspaper$thisAsItemEntity() {
		return (ItemEntity) (Object) this;
	}
}
