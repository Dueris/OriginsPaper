package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.dueris.originspaper.power.type.ModifyHarvestPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.SavedBlockPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class ServerPlayerGameModeMixin {

	@Shadow
	public ServerLevel level;
	@Shadow
	@Final
	protected ServerPlayer player;

	@Inject(method = "destroyBlock", at = @At("HEAD"))
	private void apoli$cacheMinedBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, @Share("cachedMinedBlock") @NotNull LocalRef<SavedBlockPosition> cachedMinedBlockRef, @Share("modifiedCanHarvest") @NotNull LocalBooleanRef modifiedCanHarvestRef) {
		cachedMinedBlockRef.set(new SavedBlockPosition(level, pos));
		modifiedCanHarvestRef.set(false);
	}

	@ModifyExpressionValue(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;hasCorrectToolForDrops(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1))
	private boolean apoli$modifyEffectiveTool(boolean original, @Share("cachedMinedBlock") LocalRef<SavedBlockPosition> cachedMinedBlockRef, @Share("modifiedCanHarvest") @NotNull LocalBooleanRef modifiedCanHarvestRef) {

		boolean result = PowerHolderComponent.getPowers(this.player.getBukkitEntity(), ModifyHarvestPower.class)
			.stream()
			.filter(mhp -> mhp.doesApply(cachedMinedBlockRef.get()))
			.max(ModifyHarvestPower::compareTo)
			.map(ModifyHarvestPower::isHarvestAllowed)
			.orElse(original);

		modifiedCanHarvestRef.set(result);
		return result;

	}
}
