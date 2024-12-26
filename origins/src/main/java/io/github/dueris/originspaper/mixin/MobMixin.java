package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.power.type.ActionOnItemPickupPowerType;
import io.github.dueris.originspaper.power.type.PreventItemPickupPowerType;
import io.github.dueris.originspaper.power.type.Prioritized;
import io.github.dueris.originspaper.power.type.origins.ScareCreepersPowerType;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements Targeting {

	protected MobMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@WrapWithCondition(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;pickUpItem(Lnet/minecraft/world/entity/item/ItemEntity;)V"))
	private boolean apoli$preventItemPickup(Mob mobEntity, ItemEntity itemEntity) {
		return !PreventItemPickupPowerType.doesPrevent(itemEntity, this);
	}

	@WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;pickUpItem(Lnet/minecraft/world/entity/item/ItemEntity;)V"))
	private void apoli$actionOnItemPickup(Mob mobEntity, ItemEntity itemEntity, Operation<Void> original) {

		SlotAccess stackReference = InventoryUtil.createStackReference(itemEntity.getItem());
		Entity thrower = Util.getEntityByUuid(((ItemEntityAccessor) itemEntity).getThrower(), this.getServer());

		Prioritized.CallInstance<ActionOnItemPickupPowerType> callInstance = ActionOnItemPickupPowerType.executeItemAction(thrower, stackReference, this);
		itemEntity.setItem(stackReference.get());

		original.call(mobEntity, itemEntity);
		ActionOnItemPickupPowerType.executeBiEntityAction(callInstance, thrower);

	}

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;registerGoals()V", shift = At.Shift.AFTER))
	private void origins$modifyGoals(EntityType<?> entityType, Level world, CallbackInfo ci) {

		if ((Mob) (Object) this instanceof Creeper thisAsCreeper) {
			ScareCreepersPowerType.modifyGoals(thisAsCreeper);
		}

	}
}
