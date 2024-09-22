package io.github.dueris.originspaper.mixin;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import io.github.dueris.originspaper.access.EndRespawningEntity;
import io.github.dueris.originspaper.power.type.ModifyPlayerSpawnPower;
import io.github.dueris.originspaper.power.type.PreventSleepPower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import io.github.dueris.originspaper.util.LangFile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Tuple;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.Optional;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements EndRespawningEntity {

	@Shadow
	@Final
	public MinecraftServer server;
	@Shadow
	public ServerGamePacketListenerImpl connection;
	@Shadow
	@javax.annotation.Nullable
	private BlockPos respawnPosition;
	@Shadow
	private float respawnAngle;
	@Shadow
	private ResourceKey<Level> respawnDimension;
	@Shadow
	private boolean respawnForced;
	@Unique
	private boolean apoli$isEndRespawning;

	public ServerPlayerMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Shadow
	public static Optional<ServerPlayer.RespawnPosAngle> findRespawnAndUseSpawnBlock(ServerLevel world, BlockPos pos, float spawnAngle, boolean spawnForced, boolean alive) {
		return Optional.empty();
	}

	@Shadow
	public abstract @NotNull CraftPlayer getBukkitEntity();

	@Shadow
	public abstract boolean setRespawnPosition(ResourceKey<Level> dimension, @Nullable BlockPos pos, float angle, boolean forced, boolean sendMessage, PlayerSetSpawnEvent.Cause cause);

	@Inject(method = "getBedResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setRespawnPosition(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FZZLcom/destroystokyo/paper/event/player/PlayerSetSpawnEvent$Cause;)Z"), cancellable = true)
	public void apoli$preventSleep(BlockPos blockposition, Direction enumdirection, CallbackInfoReturnable<Either<BedSleepingProblem, Unit>> cir) {
		boolean prevent = false;
		boolean respawnSet = false;
		for (PreventSleepPower power : PowerHolderComponent.gatherConditionedPowers(getBukkitEntity(), PreventSleepPower.class, (p) -> {
			return p.doesPrevent(level(), blockposition, this);
		})) {
			prevent = true;
			Component component = LangFile.translatable(power.getMessage().getString());

			if (power.doesAllowSpawnPoint() && !respawnSet) {
				setRespawnPosition(level().dimension(), blockposition, getYRot(), false, true, PlayerSetSpawnEvent.Cause.BED);
				respawnSet = true;
			}

			displayClientMessage(component, true);
		}

		if (prevent) {
			cir.setReturnValue(Either.left(Player.BedSleepingProblem.OTHER_PROBLEM));
		}
	}

	@ModifyReturnValue(method = "getRespawnDimension", at = @At("RETURN"))
	private ResourceKey<Level> apoli$modifySpawnPointDimension(ResourceKey<Level> original) {

		if (!this.apoli$isEndRespawning() && (this.respawnPosition == null || this.apoli$hasObstructedOriginalSpawnPoint())) {
			return PowerHolderComponent.getPowers(getBukkitEntity(), ModifyPlayerSpawnPower.class)
				.stream()
				.max(Comparator.comparing(ModifyPlayerSpawnPower::getPriority))
				.map(ModifyPlayerSpawnPower::getDimensionKey)
				.orElse(original);
		} else {
			return original;
		}

	}

	@ModifyReturnValue(method = "getRespawnPosition", at = @At("RETURN"))
	private BlockPos apoli$modifyRespawnPosition(BlockPos original) {

		if (this.apoli$isEndRespawning() || !PowerHolderComponent.hasPowerType(getBukkitEntity(), ModifyPlayerSpawnPower.class)) {
			return original;
		} else if (original == null) {
			return this.apoli$findPowerSpawnPoint();
		} else if (this.apoli$hasObstructedOriginalSpawnPoint()) {
			this.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
			return this.apoli$findPowerSpawnPoint();
		} else {
			return original;
		}

	}

	@ModifyReturnValue(method = "isRespawnForced", at = @At("RETURN"))
	private boolean apoli$modifySpawnForced(boolean original) {
		return original || (!this.apoli$isEndRespawning() && (respawnPosition == null || this.apoli$hasObstructedOriginalSpawnPoint()) && PowerHolderComponent.hasPowerType(this.getBukkitEntity(), ModifyPlayerSpawnPower.class));
	}

	@WrapOperation(method = "findRespawnPositionAndUseSpawnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnAndUseSpawnBlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;FZZ)Ljava/util/Optional;"))
	private Optional<ServerPlayer.RespawnPosAngle> apoli$retryObstructedSpawnPointIfFailed(ServerLevel world, BlockPos pos, float spawnAngle, boolean spawnForced, boolean alive, @NotNull Operation<Optional<ServerPlayer.RespawnPosAngle>> original) {

		Optional<ServerPlayer.RespawnPosAngle> originalRespawnPos = original.call(world, pos, spawnAngle, spawnForced, alive);

		if (originalRespawnPos.isEmpty() && PowerHolderComponent.hasPowerType(this.getBukkitEntity(), ModifyPlayerSpawnPower.class)) {
			return Optional
				.ofNullable(DismountHelper.findSafeDismountLocation(this.getType(), world, pos, spawnForced))
				.map(newPos -> ServerPlayer.RespawnPosAngle.of(newPos, pos, false, false));
		} else {
			return originalRespawnPos;
		}

	}

	@Unique
	private boolean apoli$hasObstructedOriginalSpawnPoint() {
		ServerLevel spawnPointWorld = server.getLevel(respawnDimension);
		return respawnPosition != null
			&& spawnPointWorld != null
			&& findRespawnAndUseSpawnBlock(spawnPointWorld, this.respawnPosition, this.respawnAngle, this.respawnForced, true).isEmpty();
	}

	@Unique
	private BlockPos apoli$findPowerSpawnPoint() {
		return PowerHolderComponent.getPowers(this.getBukkitEntity(), ModifyPlayerSpawnPower.class)
			.stream()
			.max(Comparator.comparing(ModifyPlayerSpawnPower::getPriority))
			.flatMap((a) -> a.getSpawn(this))
			.map(Tuple::getB)
			.orElse(null);
	}

	@Override
	public void apoli$setEndRespawning(boolean endSpawn) {
		this.apoli$isEndRespawning = endSpawn;
	}

	@Override
	public boolean apoli$isEndRespawning() {
		return this.apoli$isEndRespawning;
	}

	@Override
	public boolean apoli$hasRealRespawnPoint() {
		return respawnPosition != null && !apoli$hasObstructedOriginalSpawnPoint();
	}

}
