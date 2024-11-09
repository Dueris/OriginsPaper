package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.dueris.originspaper.access.EntityLinkedType;
import io.github.dueris.originspaper.component.OriginComponent;
import io.github.dueris.originspaper.component.PlayerOriginComponent;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.component.PowerHolderComponentImpl;
import io.github.dueris.originspaper.power.type.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Shadow
	public boolean onGround;

	@Shadow
	public abstract CraftEntity getBukkitEntity();

	@Shadow
	public abstract RegistryAccess registryAccess();

	@Shadow
	public abstract void remove(Entity.RemovalReason reason);

	@ModifyReturnValue(method = "getType", at = @At("RETURN"))
	private EntityType<?> apoli$modifyTypeTag(EntityType<?> original) {

		if (original instanceof EntityLinkedType linkedType) {
			linkedType.apoli$setEntity((Entity) (Object) this);
		}

		return original;

	}

	@Inject(method = "load", at = @At("RETURN"))
	public void apoli$loadPowerHolderComponent(CompoundTag nbt, CallbackInfo ci) {
		Entity thisAsEntity = (Entity) (Object) this;
		if (thisAsEntity instanceof LivingEntity livingEntity) {
			if (!PowerHolderComponent.KEY.isProvidedBy(thisAsEntity)) {
				PowerHolderComponent.KEY.put(thisAsEntity, new PowerHolderComponentImpl(livingEntity));
			}

			PowerHolderComponent.KEY.get(thisAsEntity).readFromNbt(nbt, registryAccess());
			if (thisAsEntity instanceof Player player) {
				if (!OriginComponent.ORIGIN.isProvidedBy(player)) {
					OriginComponent.ORIGIN.put(player, new PlayerOriginComponent(player));
				}

				OriginComponent.ORIGIN.get(player).readFromNbt(nbt, registryAccess());
			}
		}
	}

	@ModifyReturnValue(method = "saveWithoutId(Lnet/minecraft/nbt/CompoundTag;Z)Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"))
	public CompoundTag writePowerHolderComponent(CompoundTag original) {
		Entity thisAsEntity = (Entity) (Object) this;
		if (thisAsEntity instanceof LivingEntity livingEntity) {
			if (!PowerHolderComponent.KEY.isProvidedBy(thisAsEntity)) {
				PowerHolderComponent.KEY.put(thisAsEntity, new PowerHolderComponentImpl(livingEntity));
			}

			PowerHolderComponent.KEY.get(thisAsEntity).writeToNbt(original, registryAccess());
			if (thisAsEntity instanceof Player player) {
				if (!OriginComponent.ORIGIN.isProvidedBy(player)) {
					OriginComponent.ORIGIN.put(player, new PlayerOriginComponent(player));
				}

				OriginComponent.ORIGIN.get(player).writeToNbt(original, registryAccess());
			}
		}

		return original;
	}

	@Inject(method = "checkFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;fallOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;F)V"))
	private void invokeActionOnLand(CallbackInfo ci) {
		PowerHolderComponent.withPowerTypes((Entity) (Object) this, ActionOnLandPowerType.class, p -> true, ActionOnLandPowerType::executeAction);
	}

	@ModifyReturnValue(method = "fireImmune", at = @At("RETURN"))
	private boolean apoli$makeFullyFireImmune(boolean original) {
		return original
			|| PowerHolderComponent.hasPowerType((Entity) (Object) this, FireImmunityPowerType.class);
	}

	@Inject(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getOnPosLegacy()Lnet/minecraft/core/BlockPos;"))
	private void forceGrounded(MoverType movementType, Vec3 movement, CallbackInfo ci) {
		if (PowerHolderComponent.hasPowerType((Entity) (Object) this, GroundedPowerType.class)) {
			this.onGround = true;
		}
	}

	@ModifyReturnValue(method = "isInvisible", at = @At("RETURN"))
	private boolean apoli$invisibility(boolean original) {
		return original
			|| PowerHolderComponent.hasPowerType((Entity) (Object) this, InvisibilityPowerType.class);
	}

	@WrapOperation(method = "isInvisibleTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInvisible()Z"))
	private boolean apoli$specificallyInvisibleTo(Entity entity, Operation<Boolean> original, Player viewer) {

		List<InvisibilityPowerType> invisibilityPowers = PowerHolderComponent.getPowerTypes(entity, InvisibilityPowerType.class, true);
		if (viewer == null || invisibilityPowers.isEmpty()) {
			return original.call(entity);
		}

		return invisibilityPowers
			.stream()
			.anyMatch(p -> p.isActive() && p.doesApply(viewer));

	}

	@ModifyReturnValue(method = "isInvulnerableTo", at = @At("RETURN"))
	private boolean apoli$makeEntitiesInvulnerable(boolean original, DamageSource source) {
		return original
			|| PowerHolderComponent.hasPowerType((Entity) (Object) this, InvulnerablePowerType.class, p -> p.doesApply(source));
	}

	@Inject(method = "moveTowardsClosestSpace", at = @At(value = "NEW", target = "()Lnet/minecraft/core/BlockPos$MutableBlockPos;"), cancellable = true)
	protected void apoli$ignorePhasingEntities(double x, double y, double z, CallbackInfo ci, @Local BlockPos pos) {

		if (PowerHolderComponent.hasPowerType((Entity) (Object) this, PhasingPowerType.class, p -> p.doesApply(pos))) {
			ci.cancel();
		}

	}

	@ModifyExpressionValue(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassengerOfSameVehicle(Lnet/minecraft/world/entity/Entity;)Z"))
	private boolean apoli$preventEntityPushing(boolean original, Entity fromEntity) {
		return original || PreventEntityCollisionPowerType.doesApply(fromEntity, (Entity) (Object) this);
	}

	@ModifyReturnValue(method = "canCollideWith", at = @At("RETURN"))
	private boolean apoli$preventEntityCollision(boolean original, Entity other) {
		return !PreventEntityCollisionPowerType.doesApply((Entity) (Object) this, other) && original;
	}
}
