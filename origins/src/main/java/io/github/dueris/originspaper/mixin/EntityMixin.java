package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.dueris.calio.CraftCalio;
import io.github.dueris.originspaper.access.EntityLinkedType;
import io.github.dueris.originspaper.access.MovingEntity;
import io.github.dueris.originspaper.access.SubmergableEntity;
import io.github.dueris.originspaper.component.OriginComponent;
import io.github.dueris.originspaper.component.PlayerOriginComponent;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.component.PowerHolderComponentImpl;
import io.github.dueris.originspaper.power.type.*;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin implements MovingEntity, SubmergableEntity {

	@Shadow
	public boolean onGround;

	@Shadow
	public abstract CraftEntity getBukkitEntity();

	@Shadow
	public abstract RegistryAccess registryAccess();

	@Shadow
	public abstract void remove(Entity.RemovalReason reason);

	@Shadow @Final private Set<TagKey<Fluid>> fluidOnEyes;

	@Shadow protected Object2DoubleMap<TagKey<Fluid>> fluidHeight;

	@Shadow public abstract Vec3 position();

	@Shadow public abstract double getX();

	@Shadow public abstract double getY();

	@Shadow public abstract double getZ();

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
			|| PowerHolderComponent.hasPowerType((Entity) (Object) this, InvulnerabilityPowerType.class, p -> p.doesApply(source));
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

	@Override
	public boolean apoli$isSubmergedInLoosely(TagKey<Fluid> tag) {

		if (tag == null || fluidOnEyes == null) {
			return false;
		}

		return fluidOnEyes.contains(tag);
		//return Calio.areTagsEqual(Registry.FLUID_KEY, tag, submergedFluidTag);
	}

	@Override
	public double apoli$getFluidHeightLoosely(TagKey<Fluid> tag) {
		if(tag == null) {
			return 0;
		}
		if(fluidHeight.containsKey(tag)) {
			return fluidHeight.getDouble(tag);
		}
		for(TagKey<Fluid> ft : fluidHeight.keySet()) {
			if(CraftCalio.areTagsEqual(Registries.FLUID, ft, tag)) {
				return fluidHeight.getDouble(ft);
			}
		}
		return 0;
	}

	@Unique
	private boolean apoli$movingHorizontally;

	@Unique
	private boolean apoli$movingVertically;

	@Unique
	private double apoli$horizontalMovementValue;

	@Unique
	private double apoli$verticalMovementValue;

	@Unique
	private Vec3 apoli$prevPos;

	@Override
	public boolean apoli$isMovingHorizontally() {
		return apoli$movingHorizontally;
	}

	@Override
	public boolean apoli$isMovingVertically() {
		return apoli$movingVertically;
	}

	@Override
	public double apoli$getHorizontalMovementValue() {
		return apoli$horizontalMovementValue;
	}

	@Override
	public double apoli$getVerticalMovementValue() {
		return apoli$verticalMovementValue;
	}

	@Override
	public boolean apoli$isMoving() {
		return apoli$movingHorizontally || apoli$movingVertically;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void apoli$resetMovingFlags(CallbackInfo ci) {
		this.apoli$movingHorizontally = false;
		this.apoli$movingVertically = false;
	}

	@Inject(method = "baseTick", at = @At("TAIL"))
	private void apoli$setMovingFlags(CallbackInfo ci) {

		if (apoli$prevPos == null) {
			this.apoli$prevPos = this.position();
			return;
		}

		double dx = apoli$prevPos.x - this.getX();
		double dy = apoli$prevPos.y - this.getY();
		double dz = apoli$prevPos.z - this.getZ();

		this.apoli$horizontalMovementValue = Math.sqrt(dx * dx + dz * dz);
		this.apoli$verticalMovementValue = Math.sqrt(dy * dy);

		this.apoli$prevPos = this.position();

		if (this.apoli$horizontalMovementValue >= 0.01) {
			this.apoli$movingHorizontally = true;
		}

		if (this.apoli$verticalMovementValue >= 0.01) {
			this.apoli$movingVertically = true;
		}

	}
}
