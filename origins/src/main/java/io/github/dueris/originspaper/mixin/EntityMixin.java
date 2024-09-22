package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.dueris.originspaper.access.EntityLinkedType;
import io.github.dueris.originspaper.power.type.ActionOnLandPower;
import io.github.dueris.originspaper.power.type.FireImmunityPower;
import io.github.dueris.originspaper.power.type.InvulnerablePower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	public abstract CraftEntity getBukkitEntity();

	@Inject(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;fallOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;F)V"))
	public void apoli$actionOnLand(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition, CallbackInfo ci) {
		PowerHolderComponent.getPowers(getBukkitEntity(), ActionOnLandPower.class).forEach(p -> p.executeAction((Entity) (Object) this));
	}

	@ModifyReturnValue(method = "fireImmune", at = @At("RETURN"))
	private boolean apoli$makeFullyFireImmune(boolean original) {
		return original
			|| PowerHolderComponent.hasPowerType(((Entity) (Object) this).getBukkitEntity(), FireImmunityPower.class);
	}

	@ModifyReturnValue(method = "isInvulnerableTo", at = @At("RETURN"))
	private boolean apoli$makeEntitiesInvulnerable(boolean original, DamageSource source) {
		return original
			|| PowerHolderComponent.doesHaveConditionedPower(((Entity) (Object) this).getBukkitEntity(), InvulnerablePower.class, p -> p.doesApply(source));
	}

	@ModifyReturnValue(method = "getType", at = @At("RETURN"))
	private EntityType<?> apoli$modifyTypeTag(EntityType<?> original) {

		if (original instanceof EntityLinkedType linkedType) {
			linkedType.apoli$setEntity((Entity) (Object) this);
		}

		return original;

	}
}
