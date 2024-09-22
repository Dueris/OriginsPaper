package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.dueris.originspaper.power.type.PreventBlockPlacePower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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

		boolean cancel = false;
		for (PreventBlockPlacePower power : PowerHolderComponent.getPowers(playerEntity.getBukkitEntity(), PreventBlockPlacePower.class)) {
			if (power.doesPrevent(playerEntity, stack, hand, toPos, onPos, direction)) {
				power.executeActions(playerEntity, hand, toPos, onPos, direction);
				cancel = true;
			}
		}

		return !cancel && original;

	}
}
