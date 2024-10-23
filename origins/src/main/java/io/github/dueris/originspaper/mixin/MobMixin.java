package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.dueris.originspaper.power.type.ActionOnItemPickupPowerType;
import io.github.dueris.originspaper.power.type.PreventItemPickupPowerType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements Targeting {

	protected MobMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@WrapWithCondition(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;pickUpItem(Lnet/minecraft/world/entity/item/ItemEntity;)V"))
	private boolean apoli$onItemPickup(Mob instance, ItemEntity itemEntity) {

		if (PreventItemPickupPowerType.doesPrevent(itemEntity, this)) {
			return false;
		}

		ActionOnItemPickupPowerType.executeActions(itemEntity, this);
		return true;

	}

}
