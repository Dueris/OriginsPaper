package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.locator.impl.HLocatorReturn;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.power.PreventBlockPlacePower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.Reflector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

@Mixin(BlockItem.class)
public class BlockItemMixin {

	@Inject(locator = HLocatorReturn.class, method = "canPlace")
	public static void canPlace$apoli(BlockItem item, BlockPlaceContext context, BlockState state, @NotNull CallbackInfo info) {
		Player player = context.getPlayer();
		if (player == null) {
			return;
		}

		Direction direction = context.getClickedFace();
		ItemStack stack = context.getItemInHand();
		InteractionHand hand = context.getHand();

		HitResult hitResult = (HitResult) Reflector.accessMethod$Invoke("getHitResult", UseOnContext.class, context);
		BlockPos toPos = context.getClickedPos();
		BlockPos onPos = ((BlockHitResult) hitResult).getBlockPos();

		boolean cancel = false;
		for (PreventBlockPlacePower power : PowerHolderComponent.getPowers(player.getBukkitEntity(), PreventBlockPlacePower.class)) {
			if (power.doesPrevent(player, stack, hand, toPos, onPos, direction)) {
				power.executeActions(player, hand, toPos, onPos, direction);
				cancel = true;
			}
		}

		if (cancel) {
			info.setReturnValue(false);
			info.setReturned(true);
		}
		return;
	}
}
