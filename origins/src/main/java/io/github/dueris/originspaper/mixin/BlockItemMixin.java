package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BlockItem.class)
public class BlockItemMixin {

	@ModifyReturnValue(method = "canPlace", at = @At("RETURN"))
	private boolean apoli$preventBlockPlace(boolean original, @NotNull BlockPlaceContext context, BlockState state) {

		Player playerEntity = context.getPlayer();
		if (playerEntity == null) {
			return original;
		}

		Direction direction = context.getClickedFace();
		ItemStack stack = context.getItemInHand();
		InteractionHand hand = context.getHand();

		BlockPos toPos = context.getClickedPos();
		BlockPos onPos = ((ItemUsageContextAccessor) context).callGetHitResult().getBlockPos();

		Prioritized.CallInstance<ActiveInteractionPowerType> aipci = new Prioritized.CallInstance<>();
		int preventBlockPlacePowers = 0;

		aipci.add(playerEntity, PreventBlockPlacePowerType.class, pbpp -> pbpp.doesPrevent(stack, hand, toPos, onPos, direction));

		for (int i = aipci.getMaxPriority(); i >= aipci.getMinPriority(); i--) {

			if (!aipci.hasPowerTypes(i)) {
				continue;
			}

			List<PreventBlockPlacePowerType> pbpps = aipci.getPowerTypes(i)
				.stream()
				.filter(p -> p instanceof PreventBlockPlacePowerType)
				.map(p -> (PreventBlockPlacePowerType) p)
				.toList();

			preventBlockPlacePowers += pbpps.size();
			pbpps.forEach(pbpp -> pbpp.executeActions(hand, toPos, onPos, direction));

		}

		return preventBlockPlacePowers <= 0 && original;

	}

	@Inject(method = "place", at = @At("RETURN"))
	private void originspaper$fixApoliPreventSync(@NotNull BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
		if (context.getPlayer() != null) {
			((ServerPlayer) context.getPlayer()).getBukkitEntity().updateInventory();
		}
	}

	@Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
	private void apoli$actionOnBlockPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir, @Local Player user, @Local BlockPos toPos, @Local ItemStack stack, @Share("aipci") LocalRef<Prioritized.CallInstance<ActiveInteractionPowerType>> aipciRef) {

		if (user == null) {
			return;
		}

		Direction direction = context.getClickedFace();
		BlockPos onPos = ((ItemUsageContextAccessor) context).callGetHitResult().getBlockPos();
		InteractionHand hand = context.getHand();

		Prioritized.CallInstance<ActiveInteractionPowerType> aipci = new Prioritized.CallInstance<>();
		aipci.add(user, ActionOnBlockPlacePowerType.class, aobpp -> aobpp.shouldExecute(stack, hand, toPos, onPos, direction));

		for (int i = aipci.getMaxPriority(); i >= aipci.getMinPriority(); i--) {
			aipci.getPowerTypes(i)
				.stream()
				.filter(p -> p instanceof ActionOnBlockPlacePowerType)
				.forEach(p -> ((ActionOnBlockPlacePowerType) p).executeOtherActions(toPos, onPos, direction));
		}

		aipciRef.set(aipci);

	}

	@Inject(method = "place", at = @At("TAIL"))
	private void apoli$actionOnBlockPlacePost(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir, @Share("aipci") @NotNull LocalRef<Prioritized.CallInstance<ActiveInteractionPowerType>> aipciRef) {

		Prioritized.CallInstance<ActiveInteractionPowerType> aipci = aipciRef.get();

		for (int i = aipci.getMaxPriority(); i >= aipci.getMinPriority(); i--) {
			aipci.getPowerTypes(i)
				.stream()
				.filter(p -> p instanceof ActionOnBlockPlacePowerType)
				.forEach(p -> ((ActionOnBlockPlacePowerType) p).executeItemActions(context.getHand()));
		}

	}

	@WrapOperation(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"))
	private InteractionResultHolder<ItemStack> apoli$preventItemUseIfFoodBlockItem(BlockItem instance, Level world, @NotNull Player user, InteractionHand hand, Operation<InteractionResultHolder<ItemStack>> original) {
		ItemStack handStack = user.getItemInHand(hand);
		return PowerHolderComponent.hasPowerType(user, PreventItemUsePowerType.class, p -> p.doesPrevent(handStack))
			? InteractionResultHolder.fail(handStack)
			: original.call(instance, world, user, hand);
	}
}
