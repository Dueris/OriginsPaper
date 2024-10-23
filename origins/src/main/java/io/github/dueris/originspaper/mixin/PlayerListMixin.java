package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.PowerManager;
import io.github.dueris.originspaper.power.type.PowerType;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {

	@Shadow
	public abstract List<ServerPlayer> getPlayers();

	@Inject(method = "reloadResources", at = @At("HEAD"))
	public void updatePowerManager(CallbackInfo ci) {
		for (ServerPlayer player : getPlayers()) {
			PowerManager.updateData(player, false);
		}
	}

	@Inject(method = "respawn(Lnet/minecraft/server/level/ServerPlayer;ZLnet/minecraft/world/entity/Entity$RemovalReason;Lorg/bukkit/event/player/PlayerRespawnEvent$RespawnReason;Lorg/bukkit/Location;)Lnet/minecraft/server/level/ServerPlayer;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;spawnIn(Lnet/minecraft/world/level/Level;)V"))
	private void apoli$invokeOnRespawnPowerCallback(ServerPlayer entityplayer, boolean flag, Entity.RemovalReason entity_removalreason, PlayerRespawnEvent.RespawnReason reason, Location location, CallbackInfoReturnable<ServerPlayer> cir, @Local(ordinal = 1) ServerPlayer newPlayer) {
		if (!flag) {
			PowerHolderComponent.KEY.get(newPlayer).getPowerTypes().forEach(PowerType::onRespawn);
		}
	}

	@Inject(method = "remove(Lnet/minecraft/server/level/ServerPlayer;Lnet/kyori/adventure/text/Component;)Lnet/kyori/adventure/text/Component;", at = @At(value = "INVOKE", target = "Lorg/bukkit/craftbukkit/entity/CraftPlayer;disconnect(Ljava/lang/String;)V"))
	private void apoli$invokeOnLeavePowerCallback(ServerPlayer entityplayer, Component leaveMessage, CallbackInfoReturnable<Component> cir) {
		PowerHolderComponent.KEY.get(entityplayer).getPowerTypes().forEach(PowerType::onLeave);
	}

}
