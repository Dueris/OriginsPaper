package io.github.dueris.originspaper.mixin;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import com.mojang.datafixers.util.Either;
import io.github.dueris.originspaper.power.PreventSleepPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

	@Inject(method = "getBedResult", locator = At.Value.SET_BED_RESPAWN_POS)
	public static void apoli$preventSleep(ServerPlayer player, BlockPos blockposition, Direction enumdirection, CallbackInfo info) {
		boolean prevent = false;
		boolean respawnSet = false;
		for (PreventSleepPower power : PowerHolderComponent.gatherConditionedPowers(player.getBukkitEntity(), PreventSleepPower.class, (p) -> {
			return p.doesPrevent(player.level(), blockposition);
		})) {
			prevent = true;
			Component component = power.getMessage();

			if (power.doesAllowSpawnPoint() && !respawnSet) {
				player.setRespawnPosition(player.level().dimension(), blockposition, player.getYRot(), false, true, PlayerSetSpawnEvent.Cause.BED);
				respawnSet = true;
			}

			player.displayClientMessage(component, true);
		}

		if (prevent) {
			info.setReturned(true);
			info.setReturnValue(Either.left(Player.BedSleepingProblem.OTHER_PROBLEM));
		}
	}
}
