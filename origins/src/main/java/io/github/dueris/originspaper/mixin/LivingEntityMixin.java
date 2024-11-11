package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.access.EntityLinkedItemStack;
import io.github.dueris.originspaper.access.JumpingEntity;
import io.github.dueris.originspaper.access.ModifiableFoodEntity;
import io.github.dueris.originspaper.access.OwnableAttributeContainer;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.component.item.ItemPowersComponent;
import io.github.dueris.originspaper.data.ApoliDamageTypes;
import io.github.dueris.originspaper.power.type.*;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.attribute.CraftAttribute;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ModifiableFoodEntity, JumpingEntity {

	@Shadow
	private ItemStack lastBodyItemStack;
	@Shadow
	private Optional<BlockPos> lastClimbablePos;
	@Unique
	private boolean originspaper$prevPowderSnowState = false;
	@Unique
	private boolean apoli$hasModifiedDamage;
	@Unique
	private Optional<Boolean> apoli$shouldApplyArmor = Optional.empty();
	@Unique
	private Optional<Boolean> apoli$shouldDamageArmor = Optional.empty();
	@Shadow
	@Final
	private AttributeMap attributes;
	@Unique
	private List<ModifyFoodPowerType> apoli$currentModifyFoodPowers = new LinkedList<>();
	@Unique
	private ItemStack apoli$originalFoodStack;
	@Unique
	private boolean apoli$applySprintJumpingEffects;

	public LivingEntityMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Shadow
	protected abstract ItemStack getLastHandItem(EquipmentSlot slot);

	@Shadow
	protected abstract ItemStack getLastArmorItem(EquipmentSlot slot);

	@Shadow
	public abstract void remove(RemovalReason reason);

	@Shadow
	public abstract boolean addEffect(MobEffectInstance mobeffect, @org.jetbrains.annotations.Nullable Entity entity, EntityPotionEffectEvent.Cause cause, boolean fireEvent);

	@Shadow
	protected abstract void hurtArmor(DamageSource source, float amount);

	@Shadow
	public abstract AttributeMap getAttributes();

	@Shadow
	public abstract CraftLivingEntity getBukkitLivingEntity();

	@Shadow
	public abstract double getAttributeValue(Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute);

	@Shadow
	public abstract float getJumpBoostPower();

	@Shadow
	public abstract void setHealth(float health);

	@WrapOperation(method = "detectEquipmentUpdatesPublic", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;collectEquipmentChanges()Ljava/util/Map;"))
	private Map<EquipmentSlot, ItemStack> apoli$updateItemStackPowers(LivingEntity instance, @NotNull Operation<Map<EquipmentSlot, ItemStack>> original) {
		Map<EquipmentSlot, ItemStack> map = original.call(instance);

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

	@Inject(method = "baseTick", at = @At("TAIL"))
	private void updateItemStackHolder(CallbackInfo ci) {
		InventoryUtil.forEachStack(this, stack -> ((EntityLinkedItemStack) stack).apoli$setEntity(this));
	}

	@Inject(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;die(Lnet/minecraft/world/damagesource/DamageSource;)V"))
	private void invokeDeathAction(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		PowerHolderComponent.withPowerTypes(this, ActionOnDeathPowerType.class, p -> p.doesApply(source.getEntity(), source, amount), p -> p.onDeath(source.getEntity()));
	}

	@Inject(method = "hurt", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSleeping()Z")))
	private void apoli$invokeHitActions(DamageSource source, float amount, @NotNull CallbackInfoReturnable<Boolean> cir) {

		if (!cir.getReturnValue()) {
			return;
		}

		Entity attacker = source.getEntity();

		PowerHolderComponent.withPowerTypes(this, ActionWhenHitPowerType.class, p -> p.doesApply(attacker, source, amount), p -> p.whenHit(attacker));
		PowerHolderComponent.withPowerTypes(attacker, ActionOnHitPowerType.class, p -> p.doesApply(this, source, amount), p -> p.onHit(this));

		PowerHolderComponent.withPowerTypes(this, ActionWhenDamageTakenPowerType.class, p -> p.doesApply(source, amount), ActionWhenDamageTakenPowerType::whenHit);
		PowerHolderComponent.withPowerTypes(this, AttackerActionWhenHitPowerType.class, p -> p.doesApply(source, amount), p -> p.whenHit(attacker));

		PowerHolderComponent.withPowerTypes(attacker, SelfActionOnHitPowerType.class, p -> p.doesApply(this, source, amount), SelfActionOnHitPowerType::onHit);
		PowerHolderComponent.withPowerTypes(attacker, TargetActionOnHitPowerType.class, p -> p.doesApply(this, source, amount), p -> p.onHit(this));

	}

	@ModifyReturnValue(method = "isSuppressingSlidingDownLadder", at = @At("RETURN"))
	private boolean apoli$overrideClimbHold(boolean original) {

		List<ClimbingPowerType> climbingPowers = PowerHolderComponent.getPowerTypes(this, ClimbingPowerType.class);
		if (climbingPowers.isEmpty()) {
			return original;
		}

		return climbingPowers
			.stream()
			.anyMatch(ClimbingPowerType::canHold);

	}

	@ModifyReturnValue(method = "canBeAffected", at = @At("RETURN"))
	private boolean apoli$effectImmunity(boolean original, MobEffectInstance effectInstance) {
		return original
			&& !PowerHolderComponent.hasPowerType(this, EffectImmunityPowerType.class, p -> p.doesApply(effectInstance));
	}

	@ModifyReturnValue(method = "onClimbable", at = @At("RETURN"))
	private boolean apoli$modifyClimbing(boolean original) {

		boolean modified = apoli$modifiedClimbable(original);

		if (modified) {
			this.addEffect(new MobEffectInstance(
				MobEffects.LEVITATION, 5, 1, false, false, false
			), this, EntityPotionEffectEvent.Cause.PLUGIN, false); // We don't fire event because this is to mimic climbing.
		}
		return modified;
	}

	@Unique
	public boolean apoli$modifiedClimbable(boolean original) {
		if (original) {
			return true;
		}

		List<ClimbingPowerType> climbingPowers = PowerHolderComponent.getPowerTypes(this, ClimbingPowerType.class);
		if (this.isSpectator() || climbingPowers.isEmpty()) {
			return false;
		}

		this.lastClimbablePos = Optional.of(this.blockPosition());
		return true;
	}

	@ModifyReturnValue(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
	private ItemStack apoli$modifyCustomFoodAndCleanUp(ItemStack original) {
		((EntityLinkedItemStack) original).apoli$setEntity(this);

		Optional<EdibleItemPowerType> edibleItemPower = EdibleItemPowerType.get(original, this);
		ItemStack result = original;

		modifyCustomFood:
		if (edibleItemPower.isPresent()) {

			edibleItemPower.get().executeEntityAction();

			SlotAccess newStackRef = InventoryUtil.createStackReference(original);
			SlotAccess resultStackRef = edibleItemPower.get().executeItemActions(newStackRef);

			ItemStack newStack = newStackRef.get();
			ItemStack resultStack = resultStackRef.get();

			if (resultStackRef == SlotAccess.NULL) {
				result = newStack;
				break modifyCustomFood;
			} else if (newStack.isEmpty()) {
				result = resultStack;
				break modifyCustomFood;
			} else if (ItemStack.matches(resultStack, newStack)) {
				newStack.grow(1);
			} else if ((LivingEntity) (Object) this instanceof Player player && !player.isCreative()) {
				player.getInventory().placeItemBackInInventory(resultStack);
			} else {
				InventoryUtil.throwItem(this, resultStack, false, false);
			}

			result = newStack;

		}

		return result;

	}

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getTicksFrozen()I"))
	private void freezeEntityFromPower(CallbackInfo ci) {
		if (PowerHolderComponent.hasPowerType(this, FreezePowerType.class)) {
			this.originspaper$prevPowderSnowState = this.isInPowderSnow;
			this.isInPowderSnow = true;
		}
	}

	@Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeFrost()V"))
	private void unfreezeEntityFromPower(CallbackInfo ci) {
		if (PowerHolderComponent.hasPowerType(this, FreezePowerType.class)) {
			this.isInPowderSnow = this.originspaper$prevPowderSnowState;
		}
	}

	@Inject(method = "canFreeze", at = @At("RETURN"), cancellable = true)
	private void allowFreezingPower(CallbackInfoReturnable<Boolean> cir) {
		if (PowerHolderComponent.hasPowerType(this, FreezePowerType.class)) {
			cir.setReturnValue(true);
		}
	}

	@WrapOperation(method = "getVisibilityPercent", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isInvisible()Z"))
	private boolean apoli$specificallyInvisibleTo(LivingEntity livingEntity, Operation<Boolean> original, @Nullable Entity viewer) {

		List<InvisibilityPowerType> invisibilityPowers = PowerHolderComponent.getPowerTypes(livingEntity, InvisibilityPowerType.class, true);
		if (viewer == null || invisibilityPowers.isEmpty()) {
			return original.call(livingEntity);
		}

		return invisibilityPowers
			.stream()
			.anyMatch(p -> p.isActive() && p.doesApply(viewer));

	}

	@Inject(method = "getAttributes", at = @At("RETURN"))
	private void apoli$setAttributeContainerOwner(@NotNull CallbackInfoReturnable<AttributeMap> cir) {

		if (cir.getReturnValue() instanceof OwnableAttributeContainer ownableAttributeContainer) {
			ownableAttributeContainer.apoli$setOwner(this);
		}

	}

	@ModifyVariable(method = "hurt", at = @At("HEAD"), argsOnly = true)
	private float apoli$modifyDamageTaken(float original, DamageSource source, float amount) {

		if (source.is(ApoliDamageTypes.SYNC_DAMAGE_SOURCE)) {
			return original;
		}

		LivingEntity thisAsLiving = (LivingEntity) (Object) this;
		float newValue = original;

		if (source.getEntity() != null && source.is(DamageTypeTags.IS_PROJECTILE)) {
			newValue = PowerHolderComponent.modify(source.getEntity(), ModifyProjectileDamagePowerType.class, original,
				p -> p.doesApply(source, original, thisAsLiving),
				p -> p.executeActions(thisAsLiving));
		} else if (source.getEntity() != null) {
			newValue = PowerHolderComponent.modify(source.getEntity(), ModifyDamageDealtPowerType.class, original,
				p -> p.doesApply(source, original, thisAsLiving),
				p -> p.executeActions(thisAsLiving));
		}

		float intermediateValue = newValue;
		newValue = PowerHolderComponent.modify(this, ModifyDamageTakenPowerType.class, intermediateValue,
			p -> p.doesApply(source, intermediateValue),
			p -> p.executeActions(source.getEntity()));

		apoli$hasModifiedDamage = newValue != original;
		List<ModifyDamageTakenPowerType> modifyDamageTakenPowers = PowerHolderComponent.getPowerTypes(this, ModifyDamageTakenPowerType.class)
			.stream()
			.filter(mdtp -> mdtp.doesApply(source, original))
			.toList();

		long wantArmor = modifyDamageTakenPowers
			.stream()
			.filter(mdtp -> mdtp.modifiesArmorApplicance() && mdtp.shouldApplyArmor())
			.count();
		long dontWantArmor = modifyDamageTakenPowers
			.stream()
			.filter(mdtp -> mdtp.modifiesArmorApplicance() && !mdtp.shouldApplyArmor())
			.count();
		apoli$shouldApplyArmor = wantArmor == dontWantArmor ? Optional.empty() : Optional.of(wantArmor > dontWantArmor);

		long wantDamage = modifyDamageTakenPowers
			.stream()
			.filter(mdtp -> mdtp.modifiesArmorDamaging() && mdtp.shouldDamageArmor())
			.count();
		long dontWantDamage = modifyDamageTakenPowers
			.stream()
			.filter(mdtp -> mdtp.modifiesArmorDamaging() && !mdtp.shouldDamageArmor())
			.count();
		apoli$shouldDamageArmor = wantDamage == dontWantDamage ? Optional.empty() : Optional.of(wantDamage > dontWantDamage);

		return newValue;

	}

	@ModifyExpressionValue(method = "hurt", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;dead:Z"))
	private boolean apoli$preventHitIfDamageIsZero(boolean original, DamageSource source, float amount) {
		return original || apoli$hasModifiedDamage && amount <= 0.0F;
	}

	@ModifyExpressionValue(method = "getDamageAfterArmorAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z"))
	private boolean apoli$allowApplyingOrDamagingArmor(boolean original, DamageSource source, float amount) {

		if (apoli$shouldApplyArmor.isEmpty() && (original && apoli$shouldDamageArmor.orElse(false))) {
			this.hurtArmor(source, amount);
		}

		return apoli$shouldApplyArmor
			.map(result -> !result)
			.orElse(original);

	}

	@ModifyExpressionValue(method = "getDamageAfterArmorAbsorb", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/damagesource/CombatRules;getDamageAfterAbsorb(Lnet/minecraft/world/entity/LivingEntity;FLnet/minecraft/world/damagesource/DamageSource;FF)F"))
	private float apoli$allowApplyingArmor(float modified, DamageSource source, float original) {
		return apoli$shouldApplyArmor.orElse(true)
			? modified
			: original;
	}

	@WrapWithCondition(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurtArmor(Lnet/minecraft/world/damagesource/DamageSource;F)V"))
	private boolean apoli$allowDamagingArmor(LivingEntity instance, DamageSource source, float amount) {
		return apoli$shouldDamageArmor.orElse(true);
	}

	@ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"), method = "travel", name = "d0", ordinal = 0)
	public double modifyFallingVelocity(double original) {

		if (this.getDeltaMovement().y >= -0.06D || this.fallDistance <= 0) {
			this.attributes.getInstance(CraftAttribute.bukkitToMinecraftHolder(Attribute.GENERIC_GRAVITY)).setBaseValue(0.08);
			return original;
		}

		for (ModifyFallingPowerType power : PowerHolderComponent.getPowerTypes(this, ModifyFallingPowerType.class)) {

			if (!power.shouldTakeFallDamage()) {
				this.fallDistance = 0;
			}

			double modified = ModifierUtil.applyModifiers(this, power.getModifiers(), original);

			this.attributes.getInstance(CraftAttribute.bukkitToMinecraftHolder(Attribute.GENERIC_GRAVITY)).setBaseValue(modified);
		}
		return getGravity();
	}

	@Override
	public List<ModifyFoodPowerType> apoli$getCurrentModifyFoodPowers() {
		return apoli$currentModifyFoodPowers;
	}

	@Override
	public void apoli$setCurrentModifyFoodPowers(List<ModifyFoodPowerType> powers) {
		apoli$currentModifyFoodPowers = powers;
	}

	@Override
	public ItemStack apoli$getOriginalFoodStack() {
		return apoli$originalFoodStack;
	}

	@Override
	public void apoli$setOriginalFoodStack(ItemStack original) {
		apoli$originalFoodStack = original;
	}

	@ModifyVariable(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), argsOnly = true)
	private ItemStack apoli$modifyEatenStack(ItemStack original) {

		LivingEntity thisAsLiving = (LivingEntity) (Object) this;
		if (thisAsLiving instanceof Player) {
			return original;
		}

		SlotAccess newStackRef = InventoryUtil.createStackReference(original);
		List<ModifyFoodPowerType> modifyFoodPowers = PowerHolderComponent.getPowerTypes(this, ModifyFoodPowerType.class)
			.stream()
			.filter(mfp -> mfp.doesApply(original))
			.toList();

		for (ModifyFoodPowerType modifyFoodPower : modifyFoodPowers) {
			modifyFoodPower.setConsumedItemStackReference(newStackRef);
		}

		this.apoli$setCurrentModifyFoodPowers(modifyFoodPowers);
		this.apoli$setOriginalFoodStack(original);

		return newStackRef.get();

	}

	@Inject(method = "addEatEffect", at = @At("HEAD"), cancellable = true)
	private void apoli$preventApplyingFoodEffects(FoodProperties component, CallbackInfo ci) {
		if (this.apoli$getCurrentModifyFoodPowers().stream().anyMatch(ModifyFoodPowerType::doesPreventEffects)) {
			ci.cancel();
		}
	}

	@ModifyVariable(method = "heal(FLorg/bukkit/event/entity/EntityRegainHealthEvent$RegainReason;Z)V", at = @At("HEAD"), argsOnly = true)
	private float modifyHealingApplied(float originalValue) {
		return PowerHolderComponent.modify(this, ModifyHealingPowerType.class, originalValue);
	}

	@Override
	public boolean apoli$applySprintJumpEffects() {
		return apoli$applySprintJumpingEffects;
	}

	@Inject(method = "getJumpPower(F)F", at = @At("HEAD"))
	private void apoli$modifyJumpVelocity(float strength, CallbackInfoReturnable<Float> cir) {
		float blockJumpFactor = this.getBlockJumpFactor();
		float jumpBoostPower = this.getJumpBoostPower();
		float original = (float) 0.41999998688697815D * strength * blockJumpFactor + jumpBoostPower;

		float modified = PowerHolderComponent.modify(this, ModifyJumpPowerType.class, original, p -> true, ModifyJumpPowerType::executeAction);
		this.apoli$applySprintJumpingEffects = modified > 0;
		Objects.requireNonNull(this.attributes.getInstance(Attributes.JUMP_STRENGTH), "Unable to build jump strength attribute!")
			.setBaseValue(
				(modified - jumpBoostPower) / (strength * blockJumpFactor)
			);
	}

	@ModifyExpressionValue(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSprinting()Z"))
	private boolean apoli$shouldApplySprintJumpEffects(boolean original) {
		return original && this.apoli$applySprintJumpEffects();
	}

	@ModifyExpressionValue(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getFriction()F"))
	private float modifySlipperiness(float original) {
		return PowerHolderComponent.modify(this, ModifySlipperinessPowerType.class, original, p -> p.doesApply(level(), getBlockPosBelowThatAffectsMyMovement()));
	}

	@ModifyVariable(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;Lorg/bukkit/event/entity/EntityPotionEffectEvent$Cause;Z)Z", at = @At("HEAD"), argsOnly = true)
	private @NotNull MobEffectInstance apoli$modifyStatusEffect(@NotNull MobEffectInstance original) {

		Holder<MobEffect> effectType = original.getEffect();

		float amplifier = PowerHolderComponent.modify(this, ModifyStatusEffectAmplifierPowerType.class, original.getAmplifier(), p -> p.doesApply(effectType));
		float duration = PowerHolderComponent.modify(this, ModifyStatusEffectDurationPowerType.class, original.getDuration(), p -> p.doesApply(effectType));

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

	@ModifyExpressionValue(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDeadOrDying()Z"))
	private boolean apoli$preventDeath(boolean original, DamageSource source, float amount) {

		if (original && PreventDeathPowerType.doesPrevent(this, source, amount)) {
			this.setHealth(1.0F);
			return false;
		}

		return original;

	}

	/* @ModifyReturnValue(method = "canBreatheUnderwater", at = @At("RETURN"))
	private boolean origins$breatheUnderwater(boolean original) {
		return original
			|| PowerHolderComponent.hasPowerType(this, WaterBreathingPowerType.class);
	}

	@Inject(method = "baseTick", at = @At("TAIL"))
	private void origins$waterBreathingTick(CallbackInfo ci) {
		WaterBreathingPowerType.tick((LivingEntity) (Object) this);
	} */ // TODO
}
