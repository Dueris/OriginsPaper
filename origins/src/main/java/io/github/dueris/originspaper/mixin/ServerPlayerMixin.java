package io.github.dueris.originspaper.mixin;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import com.dragoncommissions.mixbukkit.api.shellcode.impl.api.CallbackInfo;
import com.mojang.datafixers.util.Either;
import io.github.dueris.originspaper.power.type.PreventSleepPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.LangFile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

import static io.github.dueris.originspaper.mixin.EntityTypeMixin.PLAYER_TAG_TIE;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

	@Inject(method = "getBedResult", locator = At.Value.SET_BED_RESPAWN_POS)
	public static void apoli$preventSleep(@NotNull ServerPlayer player, BlockPos blockposition, Direction enumdirection, CallbackInfo info) {
		boolean prevent = false;
		boolean respawnSet = false;
		for (PreventSleepPower power : PowerHolderComponent.gatherConditionedPowers(player.getBukkitEntity(), PreventSleepPower.class, (p) -> {
			return p.doesPrevent(player.level(), blockposition);
		})) {
			prevent = true;
			Component component = LangFile.translatable(power.getMessage().getString());

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

	@Inject(method = "tick", locator = At.Value.HEAD)
	public static void apoli$modifyTypeTagUpdater(@NotNull ServerPlayer player, CallbackInfo info) {
		if (!PLAYER_TAG_TIE.containsKey(player.getType())) {
			PLAYER_TAG_TIE.put(player.getType(), new LinkedList<>());
			PLAYER_TAG_TIE.get(player.getType()).add(player);
		} else if (!PLAYER_TAG_TIE.get(player.getType()).contains(player)) {
			PLAYER_TAG_TIE.get(player.getType()).add(player);
		}
	}
}
