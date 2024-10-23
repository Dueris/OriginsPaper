package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.*;
import io.github.dueris.originspaper.util.BlockUsagePhase;
import io.github.dueris.originspaper.util.PriorityPhase;
import io.github.dueris.originspaper.util.SavedBlockPosition;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.WeakHashMap;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

	@Shadow
	public ServerLevel level;
	@Shadow
	@Final
	protected ServerPlayer player;
	@Unique
	private Direction apoli$blockBreakDirection;

	@Inject(method = "destroyBlock", at = @At("HEAD"))
	private void apoli$cacheMinedBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Share("cachedMinedBlock") @NotNull LocalRef<SavedBlockPosition> cachedMinedBlockRef, @Share("modifiedCanHarvest") @NotNull LocalBooleanRef modifiedCanHarvestRef) {
		cachedMinedBlockRef.set(new SavedBlockPosition(level, pos));
		modifiedCanHarvestRef.set(false);
	}

	@ModifyExpressionValue(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean apoli$modifyEffectiveTool(boolean original, @Share("cachedMinedBlock") LocalRef<SavedBlockPosition> cachedMinedBlockRef, @Share("modifiedCanHarvest") @NotNull LocalBooleanRef modifiedCanHarvestRef) {

		boolean result = PowerHolderComponent.getPowerTypes(this.player, ModifyHarvestPowerType.class)
			.stream()
			.filter(mhp -> mhp.doesApply(cachedMinedBlockRef.get()))
			.max(ModifyHarvestPowerType::compareTo)
			.map(ModifyHarvestPowerType::isHarvestAllowed)
			.orElse(original);

		modifiedCanHarvestRef.set(result);
		return result;

	}

	@Inject(method = "handleBlockBreakAction", at = @At("HEAD"))
	private void apoli$cacheBlockBreakDirection(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci) {
		this.apoli$blockBreakDirection = direction;
	}

	@Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lorg/bukkit/plugin/PluginManager;callEvent(Lorg/bukkit/event/Event;)V"))
	private void apoli$actionOnBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) boolean blockRemoved, @Share("cachedMinedBlock") LocalRef<SavedBlockPosition> cachedMinedBlockRef, @Share("modifiedCanHarvest") LocalBooleanRef modifiedCanHarvestRef) {
		boolean harvestedSuccessfully = blockRemoved && modifiedCanHarvestRef.get();
		PowerHolderComponent.withPowerTypes(this.player, ActionOnBlockBreakPowerType.class,
			aobbp -> aobbp.doesApply(cachedMinedBlockRef.get()),
			aobbp -> aobbp.executeActions(harvestedSuccessfully, pos, apoli$blockBreakDirection));
	}

	@WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;useWithoutItem(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;"))
	private InteractionResult apoli$beforeUseBlock(BlockState state, Level world, @NotNull Player player, BlockHitResult hitResult, Operation<InteractionResult> original, ServerPlayer mPlayer, Level mWorld, ItemStack mStack, InteractionHand mHand, @Share("zeroPriority$onBlock") LocalRef<InteractionResult> zeroPriority$onBlockRef, @Share("zeroPriority$itemOnBlock") LocalRef<InteractionResult> zeroPriority$itemOnBlockRef) {

		ItemStack stackInHand = player.getItemInHand(mHand);
		BlockUsagePhase usePhase = BlockUsagePhase.BLOCK;

		if (PreventBlockUsePowerType.doesPrevent(player, usePhase, hitResult, stackInHand, mHand)) {
			return InteractionResult.FAIL;
		}

		Prioritized.CallInstance<ActiveInteractionPowerType> aipci = new Prioritized.CallInstance<>();
		aipci.add(player, ActionOnBlockUsePowerType.class, p -> p.shouldExecute(usePhase, PriorityPhase.BEFORE, hitResult, mHand, stackInHand));

		for (int i = aipci.getMaxPriority(); i >= aipci.getMinPriority(); i--) {

			if (!aipci.hasPowerTypes(i)) {
				continue;
			}

			List<ActiveInteractionPowerType> aips = aipci.getPowerTypes(i);
			InteractionResult previousResult = InteractionResult.PASS;

			for (ActiveInteractionPowerType aip : aips) {

				InteractionResult currentResult = aip instanceof ActionOnBlockUsePowerType aobup
					? aobup.executeAction(hitResult, mHand)
					: InteractionResult.PASS;

				if (Util.shouldOverride(previousResult, currentResult)) {
					previousResult = currentResult;
				}

			}

			if (i == 0) {
				zeroPriority$onBlockRef.set(previousResult);
				continue;
			}

			if (previousResult == InteractionResult.PASS) {
				continue;
			}

			if (previousResult.shouldSwing()) {
				player.swing(mHand);
			}

			return previousResult;

		}

		return original.call(state, world, player, hitResult);

	}

	@ModifyReturnValue(method = "useItemOn", at = @At(value = "RETURN", ordinal = 0), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;")))
	private InteractionResult apoli$afterUseBlock(InteractionResult original, ServerPlayer player, Level world, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, @Share("zeroPriority$onBlock") LocalRef<InteractionResult> zeroPriority$onBlockRef) {

		InteractionResult zeroPriority$onBlock = zeroPriority$onBlockRef.get();
		InteractionResult newResult = InteractionResult.PASS;

		if (zeroPriority$onBlock != null && zeroPriority$onBlock != InteractionResult.PASS) {
			newResult = zeroPriority$onBlock;
		} else if (original == InteractionResult.PASS) {

			Prioritized.CallInstance<ActiveInteractionPowerType> aipci = new Prioritized.CallInstance<>();
			aipci.add(player, ActionOnBlockUsePowerType.class, p -> p.shouldExecute(BlockUsagePhase.BLOCK, PriorityPhase.AFTER, hitResult, hand, stack));

			for (int i = aipci.getMaxPriority(); i >= aipci.getMinPriority(); i--) {

				if (!aipci.hasPowerTypes(i)) {
					continue;
				}

				List<ActiveInteractionPowerType> aips = aipci.getPowerTypes(i);
				InteractionResult previousResult = InteractionResult.PASS;

				for (ActiveInteractionPowerType aip : aips) {

					InteractionResult currentResult = aip instanceof ActionOnBlockUsePowerType aobup
						? aobup.executeAction(hitResult, hand)
						: InteractionResult.PASS;

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
			player.swing(hand);
		}

		return Util.shouldOverride(original, newResult)
			? newResult
			: original;

	}

	@WrapOperation(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;useItemOn(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/ItemInteractionResult;"))
	private ItemInteractionResult apoli$beforeItemUseOnBlock(BlockState state, ItemStack stack, Level world, Player player, InteractionHand hand, BlockHitResult hitResult, Operation<ItemInteractionResult> original, @Share("zeroPriority$itemOnBlock") LocalRef<InteractionResult> zeroPriority$itemOnBlockRef) {

		BlockUsagePhase usePhase = BlockUsagePhase.ITEM;
		if (PreventBlockUsePowerType.doesPrevent(player, usePhase, hitResult, stack, hand)) {
			return ItemInteractionResult.FAIL;
		}

		Prioritized.CallInstance<ActiveInteractionPowerType> aipci = new Prioritized.CallInstance<>();
		aipci.add(player, ActionOnBlockUsePowerType.class, p -> p.shouldExecute(usePhase, PriorityPhase.BEFORE, hitResult, hand, stack));

		for (int i = aipci.getMaxPriority(); i >= aipci.getMinPriority(); i--) {

			if (!aipci.hasPowerTypes(i)) {
				continue;
			}

			List<ActiveInteractionPowerType> aips = aipci.getPowerTypes(i);
			InteractionResult previousResult = InteractionResult.PASS;

			for (ActiveInteractionPowerType aip : aips) {

				InteractionResult currentResult = aip instanceof ActionOnBlockUsePowerType aobup
					? aobup.executeAction(hitResult, hand)
					: InteractionResult.PASS;

				if (Util.shouldOverride(previousResult, currentResult)) {
					previousResult = currentResult;
				}

			}

			if (i == 0) {
				zeroPriority$itemOnBlockRef.set(previousResult);
				continue;
			}

			if (previousResult == InteractionResult.PASS) {
				continue;
			}

			if (previousResult.shouldSwing()) {
				player.swing(hand);
			}

			return switch (previousResult) {
				case SUCCESS, SUCCESS_NO_ITEM_USED -> ItemInteractionResult.SUCCESS;
				case CONSUME -> ItemInteractionResult.CONSUME;
				case CONSUME_PARTIAL -> ItemInteractionResult.CONSUME_PARTIAL;
				case FAIL -> ItemInteractionResult.FAIL;
				default -> throw new IllegalStateException("Unexpected value: " + previousResult);
			};

		}

		return original.call(state, stack, world, player, hand, hitResult);

	}

	@ModifyReturnValue(method = "useItemOn", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/ItemInteractionResult;consumesAction()Z")))
	private InteractionResult apoli$afterItemUseOnBlock(InteractionResult original, ServerPlayer player, Level world, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, @Share("zeroPriority$itemOnBlock") @NotNull LocalRef<InteractionResult> zeroPriority$itemOnBlockRef) {

		InteractionResult zeroPriority$itemOnBlock = zeroPriority$itemOnBlockRef.get();
		InteractionResult newResult = InteractionResult.PASS;

		if (zeroPriority$itemOnBlock != null && zeroPriority$itemOnBlock != InteractionResult.PASS) {
			newResult = zeroPriority$itemOnBlock;
		} else if (original == InteractionResult.PASS) {

			Prioritized.CallInstance<ActiveInteractionPowerType> aipci = new Prioritized.CallInstance<>();
			aipci.add(player, ActionOnBlockUsePowerType.class, p -> p.shouldExecute(BlockUsagePhase.ITEM, PriorityPhase.AFTER, hitResult, hand, stack));

			for (int i = aipci.getMaxPriority(); i >= aipci.getMinPriority(); i--) {

				if (!aipci.hasPowerTypes(i)) {
					continue;
				}

				List<ActiveInteractionPowerType> aips = aipci.getPowerTypes(i);
				InteractionResult previousResult = InteractionResult.PASS;

				for (ActiveInteractionPowerType aip : aips) {

					InteractionResult currentResult = aip instanceof ActionOnBlockUsePowerType aobup
						? aobup.executeAction(hitResult, hand)
						: InteractionResult.PASS;

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
			player.swing(hand);
		}

		return Util.shouldOverride(original, newResult)
			? newResult
			: original;

	}

	@Inject(method = "destroyBlock", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.AFTER))
	private void apoli$cacheCopyAndOriginalStacks(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) ItemStack originalStack, @Local(ordinal = 1) ItemStack copyStack) {
		ModifyEnchantmentLevelPowerType.COPY_TO_ORIGINAL_STACK
			.computeIfAbsent(player.getUUID(), k -> new WeakHashMap<>())
			.put(copyStack, originalStack);
	}

	@Inject(method = "destroyBlock", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;")))
	private void apoli$clearCachedCopyAndOriginalStacks(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		ModifyEnchantmentLevelPowerType.COPY_TO_ORIGINAL_STACK.remove(player.getUUID());
	}

}
