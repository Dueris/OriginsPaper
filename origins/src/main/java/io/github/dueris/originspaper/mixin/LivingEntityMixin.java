package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.data.types.modifier.ModifierUtil;
import io.github.dueris.originspaper.power.type.*;
import io.github.dueris.originspaper.power.type.simple.WaterBreathingPower;
import io.github.dueris.originspaper.storage.ItemPowersComponent;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	@Shadow
	private ItemStack lastBodyItemStack;
	@Unique
	private boolean originspaper$prevPowderSnowState = false;

	public LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Shadow
	public abstract CraftLivingEntity getBukkitLivingEntity();

	@Shadow
	public abstract void setHealth(float health);

	@Shadow
	protected abstract ItemStack getLastHandItem(EquipmentSlot slot);

	@Shadow
	protected abstract ItemStack getLastArmorItem(EquipmentSlot slot);

	@Shadow
	public abstract void remove(RemovalReason reason);

	@Shadow
	public abstract boolean hasEffect(Holder<MobEffect> effect);

	@Shadow
	public abstract Vec3 handleRelativeFrictionAndCalculateMovement(Vec3 movementInput, float slipperiness);

	@Shadow
	@Nullable
	public abstract MobEffectInstance getEffect(Holder<MobEffect> effect);

	@Shadow
	public abstract boolean shouldDiscardFriction();

	@ModifyVariable(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;Lorg/bukkit/event/entity/EntityPotionEffectEvent$Cause;Z)Z", at = @At("HEAD"), argsOnly = true)
	private @NotNull MobEffectInstance apoli$modifyStatusEffect(@NotNull MobEffectInstance original) {
		Holder<MobEffect> effectType = original.getEffect();

		org.bukkit.entity.LivingEntity living = (org.bukkit.entity.LivingEntity) getBukkitEntity();
		float amplifier = PowerHolderComponent.modify(living, ModifyStatusEffectAmplifierPower.class, original.getAmplifier(), p -> p.doesApply(effectType));
		float duration = PowerHolderComponent.modify(living, ModifyStatusEffectDurationPower.class, original.getDuration(), p -> p.doesApply(effectType));

		return new MobEffectInstance(
			effectType,
			Math.round(duration),
			Math.round(amplifier),
			original.isAmbient(),
			original.isVisible(),
			original.showIcon(),
			original.hiddenEffect
		);
	}

	@ModifyVariable(method = "heal(FLorg/bukkit/event/entity/EntityRegainHealthEvent$RegainReason;Z)V", at = @At("HEAD"), argsOnly = true)
	private float modifyHealingApplied(float originalValue) {
		return PowerHolderComponent.modify(getBukkitEntity(), ModifyHealingPower.class, originalValue);
	}

	@ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
	private float apoli$modifyDamageTaken(float original, @NotNull DamageSource source, float amount) {

		LivingEntity thisAsLiving = (LivingEntity) (Object) this;
		float newValue = original;

		if (source.getEntity() != null && source.is(DamageTypeTags.IS_PROJECTILE)) {
			newValue = PowerHolderComponent.modify(source.getEntity().getBukkitEntity(), ModifyProjectileDamagePower.class, original,
				p -> p.doesApply(source, original, thisAsLiving, source.getEntity()),
				p -> p.executeActions(thisAsLiving, source.getEntity()));
		} else if (source.getEntity() != null) {
			newValue = PowerHolderComponent.modify(source.getEntity().getBukkitEntity(), ModifyDamageDealtPower.class, original,
				p -> p.doesApply(source, original, thisAsLiving, source.getEntity()),
				p -> p.executeActions(thisAsLiving, source.getEntity()));
		}

		float intermediateValue = newValue;
		newValue = PowerHolderComponent.modify(thisAsLiving.getBukkitEntity(), ModifyDamageTakenPower.class, intermediateValue,
			p -> p.doesApply(source, thisAsLiving, intermediateValue),
			p -> p.executeActions(source.getEntity(), thisAsLiving));

		return newValue;

	}

	@Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;die(Lnet/minecraft/world/damagesource/DamageSource;)V"))
	private void invokeDeathAction(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity thisAsLiving = (LivingEntity) (Object) this;
		for (ActionOnDeathPower power : PowerHolderComponent.getPowers(this.getBukkitLivingEntity(), ActionOnDeathPower.class)) {
			if (power.doesApply(source.getEntity(), source, amount, thisAsLiving)) {
				power.onDeath(source.getEntity(), thisAsLiving);
			}
		}
	}

	@Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;die(Lnet/minecraft/world/damagesource/DamageSource;)V"))
	private void invokeKillAction(@NotNull DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity thisAsLiving = (LivingEntity) (Object) this;
		if (source.getEntity() == null) return;
		for (SelfActionOnKillPower power : PowerHolderComponent.getPowers(source.getEntity().getBukkitEntity(), SelfActionOnKillPower.class)) {
			if (power.doesApply(thisAsLiving, source.getEntity(), source, amount)) {
				power.executeAction(source.getEntity());
			}
		}
	}

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getTicksFrozen()I"))
	private void freezeEntityFromPower(CallbackInfo ci) {
		if (PowerHolderComponent.doesHaveConditionedPower(this.getBukkitLivingEntity(), FreezePower.class, p -> p.isActive(this))) {
			this.originspaper$prevPowderSnowState = this.isInPowderSnow;
			this.isInPowderSnow = true;
		}
	}

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeFrost()V"))
	private void unfreezeEntityFromPower(CallbackInfo ci) {
		if (PowerHolderComponent.doesHaveConditionedPower(this.getBukkitLivingEntity(), FreezePower.class, p -> p.isActive(this))) {
			this.isInPowderSnow = this.originspaper$prevPowderSnowState;
		}
	}

	@Inject(method = "canFreeze", at = @At("RETURN"), cancellable = true)
	private void allowFreezingPower(CallbackInfoReturnable<Boolean> cir) {
		if (PowerHolderComponent.doesHaveConditionedPower(this.getBukkitLivingEntity(), FreezePower.class, p -> p.isActive(this))) {
			cir.setReturnValue(true);
		}
	}

	@ModifyReturnValue(method = "canBeAffected", at = @At("RETURN"))
	private boolean apoli$effectImmunity(boolean original, MobEffectInstance effectInstance) {
		return original
			&& !PowerHolderComponent.doesHaveConditionedPower(this.getBukkitLivingEntity(), EffectImmunityPower.class, p -> p.doesApply(effectInstance, this));
	}

	@ModifyExpressionValue(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDeadOrDying()Z"))
	public boolean apoli$preventDeath(boolean original, DamageSource source, float amount) {

		if (original && PreventDeathPower.doesPrevent(this, source, amount)) {
			this.setHealth(1.0F);
			return false;
		}

		return original;

	}

	@Inject(method = "travel", at = @At("HEAD"))
	public void apoli$modifyFalling(Vec3 movementInput, CallbackInfo ci) {
		if (((LivingEntity) (Object) this) instanceof Player player) {

			if (player.getDeltaMovement().y >= -0.06D || player.fallDistance <= 0) {
				player.getBukkitEntity().getAttribute(Attribute.GENERIC_GRAVITY).setBaseValue(player.getBukkitEntity().getAttribute(Attribute.GENERIC_GRAVITY).getDefaultValue());
				return;
			}

			for (ModifyFallingPowerType power : PowerHolderComponent.getPowers(player.getBukkitEntity(), ModifyFallingPowerType.class)) {

				if (!power.shouldTakeFallDamage()) {
					player.fallDistance = 0;
				}

				double original = player.getBukkitEntity().getAttribute(Attribute.GENERIC_GRAVITY).getDefaultValue();
				double modified = ModifierUtil.applyModifiers(player, power.getModifiers(), original);

				player.getBukkitEntity().getAttribute(Attribute.GENERIC_GRAVITY).setBaseValue(modified);
			}
		}
	}

	@ModifyReturnValue(method = "canBreatheUnderwater", at = @At("RETURN"))
	private boolean origins$waterBreathing(boolean original) {
		return original || PowerHolderComponent.hasPower(getBukkitEntity(), "origins:water_breathing");
	}

	@Inject(method = "baseTick", at = @At("RETURN"))
	public void origins$tickWaterBreathe(CallbackInfo ci) {
		WaterBreathingPower.tick((LivingEntity) (Object) this);
	}

	@WrapOperation(method = "detectEquipmentUpdatesPublic", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;collectEquipmentChanges()Ljava/util/Map;"))
	private Map<EquipmentSlot, ItemStack> apoli$updateItemStackPowers(LivingEntity instance, @NotNull Operation<Map<EquipmentSlot, ItemStack>> original) {
		Map<EquipmentSlot, ItemStack> map = original.call(instance);

		if ((Object) this != instance) {
			throw new RuntimeException("Uhhhh... Why are we not the same bro?");
		}

		if (map != null) {
			if (instance instanceof ServerPlayer) {
				map.forEach((slot, newStack) -> {
					try {
						ItemStack itemstack = switch (slot.getType()) {
							case HAND -> getLastHandItem(slot);
							case HUMANOID_ARMOR -> getLastArmorItem(slot);
							case ANIMAL_ARMOR -> lastBodyItemStack;
						};

						ItemPowersComponent.onChangeEquipment(instance, slot, itemstack, newStack);
					} catch (Exception e) {
						throw new RuntimeException("Unable to run power equipment change!", e);
					}
				});
			}
		}

		return map;
	}

	@WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;shouldDiscardFriction()Z"))
	private boolean useApoliFrictionCalc$modifySlipperiness(@NotNull LivingEntity instance, Operation<Boolean> original) {
		return PowerHolderComponent.hasPowerType(instance.getBukkitEntity(), ModifySlipperinessPower.class) || original.call(instance);
	}

	@WrapOperation(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V", ordinal = 2))
	private void modifySlipperiness(@NotNull LivingEntity instance, double a, double b, double c, Operation<Void> original) {
		BlockPos blockposition = this.getBlockPosBelowThatAffectsMyMovement();
		float f4 = this.level().getBlockState(blockposition).getBlock().getFriction();

		float f = this.onGround() ? f4 * 0.91F : 0.91F;

		if (PowerHolderComponent.hasPowerType(getBukkitEntity(), ModifySlipperinessPower.class)) {
			f = PowerHolderComponent.modify(getBukkitEntity(), ModifySlipperinessPower.class, f, p -> p.doesApply(level(), getBlockPosBelowThatAffectsMyMovement(), this));

			float x = (float) (a * (double) f);
			float y = (float) (instance instanceof FlyingAnimal ? b * (double) f : b * 0.9800000190734863D);
			float z = (float) (c * (double) f);
			getBukkitEntity().setVelocity(new Vector(x, y, z));
		} else {
			original.call(instance, a, b, c);
		}
	}

}
