package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;
import io.github.dueris.originspaper.access.JumpingEntity;
import io.github.dueris.originspaper.access.ModifiableFoodEntity;
import io.github.dueris.originspaper.access.PhasingEntity;
import io.github.dueris.originspaper.access.PlayerTiedAbilities;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.*;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.originspaper.util.PriorityPhase;
import io.github.dueris.originspaper.util.Util;
import io.github.dueris.originspaper.util.modifier.Modifier;
import io.github.dueris.originspaper.util.modifier.ModifierUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
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

import java.util.List;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements JumpingEntity {

	@Shadow
	@Final
	private Abilities abilities;
	@Unique
	private boolean apoli$updateStatsManually = false;

	protected PlayerMixin(EntityType<? extends LivingEntity> type, Level world) {
		super(type, world);
	}

	@Shadow
	public abstract Abilities getAbilities();

	@WrapOperation(method = "interactOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;interact(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;"))
	private InteractionResult apoli$beforeEntityUse(Entity entity, @NotNull Player player, InteractionHand hand, Operation<InteractionResult> original, @Share("zeroPriority$onEntity") LocalRef<InteractionResult> sharedZeroPriority$onEntity) {

		ItemStack stackInHand = player.getItemInHand(hand);
		for (PreventEntityUsePowerType peup : PowerHolderComponent.getPowerTypes(this, PreventEntityUsePowerType.class)) {

			if (peup.doesApply(entity, hand, stackInHand)) {
				return peup.executeAction(entity, hand);
			}

		}

		for (PreventBeingUsedPowerType pbup : PowerHolderComponent.getPowerTypes(entity, PreventBeingUsedPowerType.class)) {

			if (pbup.doesApply(player, hand, stackInHand)) {
				return pbup.executeAction(player, hand);
			}

		}

		Prioritized.CallInstance<ActiveInteractionPowerType> aipci = new Prioritized.CallInstance<>();

		aipci.add(player, ActionOnEntityUsePowerType.class, p -> p.shouldExecute(entity, hand, stackInHand, PriorityPhase.BEFORE));
		aipci.add(entity, ActionOnBeingUsedPowerType.class, p -> p.shouldExecute(player, hand, stackInHand, PriorityPhase.BEFORE));

		for (int i = aipci.getMaxPriority(); i >= aipci.getMinPriority(); i--) {

			if (!aipci.hasPowerTypes(i)) {
				continue;
			}

			List<ActiveInteractionPowerType> aips = aipci.getPowerTypes(i);
			InteractionResult previousResult = InteractionResult.PASS;

			for (ActiveInteractionPowerType aip : aips) {

				InteractionResult currentResult = InteractionResult.PASS;
				if (aip instanceof ActionOnEntityUsePowerType aoeup) {
					currentResult = aoeup.executeAction(entity, hand);
				} else if (aip instanceof ActionOnBeingUsedPowerType aobup) {
					currentResult = aobup.executeAction(player, hand);
				}

				if (Util.shouldOverride(previousResult, currentResult)) {
					previousResult = currentResult;
				}

			}

			if (i == 0) {
				sharedZeroPriority$onEntity.set(previousResult);
				continue;
			}

			if (previousResult == InteractionResult.PASS) {
				continue;
			}

			if (previousResult.shouldSwing()) {
				this.swing(hand);
			}

			return previousResult;

		}

		return original.call(entity, player, hand);

	}

	@ModifyReturnValue(method = "interactOn", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", ordinal = 0)))
	private InteractionResult apoli$afterEntityUse(InteractionResult original, Entity entity, InteractionHand hand, @Share("zeroPriority$onEntity") @NotNull LocalRef<InteractionResult> sharedZeroPriority$onEntity) {

		InteractionResult cachedPriorityZeroResult = sharedZeroPriority$onEntity.get();
		InteractionResult newResult = InteractionResult.PASS;

		if (cachedPriorityZeroResult != null && cachedPriorityZeroResult != InteractionResult.PASS) {
			newResult = cachedPriorityZeroResult;
		} else if (original == InteractionResult.PASS) {

			ItemStack stackInHand = this.getItemInHand(hand);
			Prioritized.CallInstance<ActiveInteractionPowerType> aipci = new Prioritized.CallInstance<>();

			aipci.add(this, ActionOnEntityUsePowerType.class, p -> p.shouldExecute(entity, hand, stackInHand, PriorityPhase.AFTER));
			aipci.add(entity, ActionOnBeingUsedPowerType.class, p -> p.shouldExecute((Player) (Object) this, hand, stackInHand, PriorityPhase.AFTER));

			for (int i = aipci.getMaxPriority(); i >= aipci.getMinPriority(); i--) {

				if (!aipci.hasPowerTypes(i)) {
					continue;
				}

				List<ActiveInteractionPowerType> aips = aipci.getPowerTypes(i);
				InteractionResult previousResult = InteractionResult.PASS;

				for (ActiveInteractionPowerType aip : aips) {

					InteractionResult currentResult = InteractionResult.PASS;
					if (aip instanceof ActionOnEntityUsePowerType aoeup) {
						currentResult = aoeup.executeAction(entity, hand);
					} else if (aip instanceof ActionOnBeingUsedPowerType aobup) {
						currentResult = aobup.executeAction((Player) (Object) this, hand);
					}

					if (Util.shouldOverride(previousResult, currentResult)) {
						previousResult = currentResult;
					}

				}

				if (previousResult != InteractionResult.PASS) {
					newResult = previousResult;
					break;
				}

			}

		}

		if (newResult.shouldSwing()) {
			this.swing(hand);
		}

		return Util.shouldOverride(original, newResult)
			? newResult
			: original;

	}

	@Inject(method = "stopSleepInBed", at = @At("HEAD"))
	private void invokeWakeUpAction(boolean bl, boolean updateSleepingPlayers, CallbackInfo ci) {
		if (!bl && !updateSleepingPlayers && getSleepingPos().isPresent()) {
			BlockPos sleepingPos = getSleepingPos().get();
			PowerHolderComponent.getPowerTypes(this, ActionOnWakeUpPowerType.class).stream().filter(p -> p.doesApply(sleepingPos)).forEach(p -> p.executeActions(sleepingPos, Direction.DOWN));
		}
	}

	@ModifyReturnValue(method = "isHurt", at = @At("RETURN"))
	private boolean apoli$disableFoodRegen(boolean original) {
		return original
			&& !PowerHolderComponent.hasPowerType(this, DisableRegenPowerType.class);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void apoli$creativeFlight(CallbackInfo ci) {
		PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(this);
		if (component == null) {
			return;
		}

		ServerPlayer thisAsServer = ((ServerPlayer) (LivingEntity) this);

		boolean original = switch (thisAsServer.gameMode.getGameModeForPlayer()) {
			case CREATIVE, SPECTATOR -> true;
			default -> false;
		};
		boolean modified = original;

		if (this instanceof PhasingEntity entity && entity.apoli$isPhasing()) {
			thisAsServer.getBukkitEntity().setAllowFlight(true);
			return;
		} else if (original != thisAsServer.getBukkitEntity().getAllowFlight()) {
			thisAsServer.getBukkitEntity().setAllowFlight(original);
		}

		for (CreativeFlightPowerType power : component.getPowerTypes(CreativeFlightPowerType.class, true)) {
			modified = power.isActive();
		}
		// OriginsPaper - region start
		// Some of our rewrites for powers require this ability to be triggered
		if (!modified && !component.getPowerTypes(ElytraFlightPowerType.class, true).isEmpty()) {
			modified = true;
		}

		if (!modified && !component.getPowerTypes(GroundedPowerType.class, true).isEmpty()) {
			modified = true;
		}
		// region end

		if (modified && !original) {
			thisAsServer.getBukkitEntity().setAllowFlight(true);
		} else if (!modified && original) {
			thisAsServer.getBukkitEntity().setAllowFlight(false);
		}
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	public void apoli$linkAbilities(Level world, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
		((PlayerTiedAbilities) this.abilities).apoli$setPlayer((Player) (Object) this);
	}

	@Inject(method = "dropEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;dropAll()V"))
	private void dropAdditionalInventory(CallbackInfo ci) {
		PowerHolderComponent.withPowerTypes(this, InventoryPowerType.class, InventoryPowerType::shouldDropOnDeath, InventoryPowerType::dropItemsOnDeath);
	}

	@Inject(method = "hurt", at = @At(value = "RETURN", ordinal = 4), cancellable = true)
	private void allowDamageIfModifyingPowersExist(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

		boolean hasModifyingPower = false;

		if (source.getEntity() != null) {
			if (source.is(DamageTypeTags.IS_PROJECTILE))
				hasModifyingPower = PowerHolderComponent.hasPowerType(source.getEntity(), ModifyProjectileDamagePowerType.class, mpdp -> mpdp.doesApply(source, amount, this));
			else
				hasModifyingPower = PowerHolderComponent.hasPowerType(source.getEntity(), ModifyDamageDealtPowerType.class, mddp -> mddp.doesApply(source, amount, this));
		}

		hasModifyingPower |= PowerHolderComponent.hasPowerType(this, ModifyDamageTakenPowerType.class, mdtp -> mdtp.doesApply(source, amount));
		if (hasModifyingPower) cir.setReturnValue(super.hurt(source, amount));

	}

	@ModifyVariable(at = @At("HEAD"), method = "causeFoodExhaustion(FLorg/bukkit/event/entity/EntityExhaustionEvent$ExhaustionReason;)V", argsOnly = true)
	private float modifyExhaustion(float exhaustionIn) {
		return PowerHolderComponent.modify(this, ModifyExhaustionPowerType.class, exhaustionIn);
	}

	@ModifyVariable(method = "eat", at = @At("HEAD"), argsOnly = true)
	private @NotNull ItemStack apoli$modifyEatenStack(ItemStack original) {

		SlotAccess newStackRef = InventoryUtil.createStackReference(original);
		ModifiableFoodEntity modifiableFoodEntity = (ModifiableFoodEntity) this;

		List<ModifyFoodPowerType> modifyFoodPowers = PowerHolderComponent.getPowerTypes(this, ModifyFoodPowerType.class)
			.stream()
			.filter(mfp -> mfp.doesApply(original))
			.toList();

		for (ModifyFoodPowerType modifyFoodPower : modifyFoodPowers) {
			modifyFoodPower.setConsumedItemStackReference(newStackRef);
		}

		modifiableFoodEntity.apoli$setCurrentModifyFoodPowers(modifyFoodPowers);
		modifiableFoodEntity.apoli$setOriginalFoodStack(original);

		return newStackRef.get();

	}

	@ModifyVariable(method = "eat", at = @At("HEAD"), argsOnly = true)
	private @NotNull FoodProperties apoli$modifyFoodComponent(@NotNull FoodProperties original, Level world, ItemStack stack, @Share("modifyFoodPowers") @NotNull LocalRef<List<ModifyFoodPowerType>> sharedModifyFoodPowers) {

		List<ModifyFoodPowerType> modifyFoodPowers = ((ModifiableFoodEntity) this).apoli$getCurrentModifyFoodPowers()
			.stream()
			.filter(p -> p.doesApply(stack))
			.toList();

		sharedModifyFoodPowers.set(modifyFoodPowers);
		this.apoli$updateStatsManually = false;

		List<Modifier> nutritionModifiers = modifyFoodPowers
			.stream()
			.flatMap(p -> p.getFoodModifiers().stream())
			.toList();
		List<Modifier> saturationModifiers = modifyFoodPowers
			.stream()
			.flatMap(p -> p.getSaturationModifiers().stream())
			.toList();

		int oldNutrition = original.nutrition();
		float oldSaturation = original.saturation();

		int newNutrition = (int) ModifierUtil.applyModifiers(this, nutritionModifiers, oldNutrition);
		float newSaturation = (float) ModifierUtil.applyModifiers(this, saturationModifiers, oldSaturation);

		if (newNutrition != oldNutrition || newSaturation != oldSaturation) {
			this.apoli$updateStatsManually = true;
		}

		return new FoodProperties(
			newNutrition,
			newSaturation,
			original.canAlwaysEat(),
			original.eatSeconds(),
			original.usingConvertsTo(),
			original.effects()
		);

	}

	@Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)V", shift = At.Shift.AFTER))
	private void apoli$executeActionsAfterEating(Level world, ItemStack stack, FoodProperties foodComponent, CallbackInfoReturnable<ItemStack> cir, @Share("modifyFoodPowers") @NotNull LocalRef<List<ModifyFoodPowerType>> sharedModifyFoodPowers) {

		List<ModifyFoodPowerType> modifyFoodPowers = sharedModifyFoodPowers.get();
		if (!((Player) (Object) this instanceof ServerPlayer serverPlayer) || modifyFoodPowers == null) {
			return;
		}

		modifyFoodPowers.forEach(ModifyFoodPowerType::eat);
		if (apoli$updateStatsManually) {
			FoodData hungerManager = serverPlayer.getFoodData();
			serverPlayer.connection.send(new ClientboundSetHealthPacket(this.getHealth(), hungerManager.getFoodLevel(), hungerManager.getSaturationLevel()));
		}

	}

	@ModifyExpressionValue(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"))
	private boolean apoli$shouldApplySprintJumpEffects(boolean original) {
		return original && this.apoli$applySprintJumpEffects();
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSpectator()Z", ordinal = 0))
	public boolean apoli$disablePhysics(Player instance, @NotNull Operation<Boolean> original) {
		return original.call(instance) || (PowerHolderComponent.hasPowerType(this, PhasingPowerType.class) && ((PhasingEntity) this).apoli$isPhasing());
	}

	/* @Inject(method = "tick", at = @At("TAIL"))
	public void origins$likeWater(CallbackInfo ci) {
		Player thisAsPlayer = (Player) (Object) this;
		LikeWaterPowerType.tick(thisAsPlayer);
	}

	@ModifyExpressionValue(method = "turtleHelmetTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"))
	private boolean origins$submergedProxy(boolean original) {
		return PowerHolderComponent.hasPowerType(this, WaterBreathingPowerType.class) != original;
	} */ // TODO
}
