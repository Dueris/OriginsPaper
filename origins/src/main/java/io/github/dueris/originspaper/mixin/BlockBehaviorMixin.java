package io.github.dueris.originspaper.mixin;

import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import io.github.dueris.originspaper.power.type.ModifyBreakSpeedPower;
import io.github.dueris.originspaper.power.type.ModifyHarvestPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(BlockBehaviour.class)
public class BlockBehaviorMixin {

	@Inject(method = "getDestroyProgress", locator = At.Value.HEAD)
	public static void apoli$modifyBlockSpeed(BlockBehaviour behaviour, @NotNull BlockState state, Player player, BlockGetter world, BlockPos pos, CallbackInfo info) {
		float f = modifyBreakSpeed(player, pos, state.getDestroySpeed(world, pos), true);

		if (f == -1.0F) {
			info.setReturnValue(modifyBreakSpeed(player, pos, 0.0F, false));
			info.setReturned(true);
		} else {
			AtomicBoolean modifyHarvest = new AtomicBoolean(false);
			PowerHolderComponent.getPowers(player.getBukkitEntity(), ModifyHarvestPower.class).stream()
				.filter(modifyHarvestPower -> modifyHarvestPower.doesApply(pos, player))
				.map(ModifyHarvestPower::isHarvestAllowed).forEach(modifyHarvest::set);
			int i = (modifyHarvest.get() || player.hasCorrectToolForDrops(state)) ? 30 : 100;

			info.setReturnValue(modifyBreakSpeed(player, pos, player.getDestroySpeed(state) / f / (float) i, false));
			info.setReturned(true);
		}
	}

	private static float modifyBreakSpeed(@NotNull Player player, BlockPos pos, float original, boolean hardness) {
		for (ModifyBreakSpeedPower power : PowerHolderComponent.getPowers(player.getBukkitEntity(), ModifyBreakSpeedPower.class)) {
			power.applyPower(pos, player, hardness);
		}

		return original;
	}
}
