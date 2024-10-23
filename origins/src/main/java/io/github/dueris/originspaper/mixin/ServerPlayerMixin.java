package io.github.dueris.originspaper.mixin;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import io.github.dueris.originspaper.access.EndRespawningEntity;
import io.github.dueris.originspaper.access.PhasingEntity;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.ActionOnItemUsePowerType;
import io.github.dueris.originspaper.power.type.ModifyPlayerSpawnPowerType;
import io.github.dueris.originspaper.power.type.PhasingPowerType;
import io.github.dueris.originspaper.power.type.PreventSleepPowerType;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.originspaper.util.PriorityPhase;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Tuple;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static io.github.dueris.originspaper.power.type.PhasingPowerType.offsets;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player implements EndRespawningEntity, PhasingEntity {

	@Shadow
	@Final
	public MinecraftServer server;
	@Shadow
	public ServerGamePacketListenerImpl connection;
	@Shadow
	@Final
	public ServerPlayerGameMode gameMode;
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
	@Unique
	private boolean apoli$isPhasing;

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

	@Shadow
	public abstract boolean setGameMode(GameType gameMode);

	@Shadow
	public abstract ServerLevel serverLevel();

	@Shadow
	public abstract void sendSystemMessage(Component message, boolean overlay);

	@Shadow
	public abstract void sendChatMessage(OutgoingChatMessage message, boolean filterMaskEnabled, ChatType.Bound params);

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

	@Override
	public void apoli$setPhasing(boolean apoli$isPhasing) {
		this.apoli$isPhasing = apoli$isPhasing;
	}

	@Override
	public boolean apoli$isPhasing() {
		return apoli$isPhasing;
	}

	@ModifyArg(method = "drop(Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"))
	private ItemStack checkItemUsageStopping(ItemStack original, @Share("prevSelectedStack") @NotNull LocalRef<ItemStack> prevSelectedStackLocRef) {

		ItemStack prevSelectedStack = prevSelectedStackLocRef.get();
		if (!this.isUsingItem() || ItemStack.matches(prevSelectedStack, this.getInventory().getSelected())) {
			return original;
		}

		SlotAccess newSelectedStackRef = InventoryUtil.createStackReference(original);
		ActionOnItemUsePowerType.executeActions(this, newSelectedStackRef, prevSelectedStack, ActionOnItemUsePowerType.TriggerType.STOP, PriorityPhase.ALL);

		return newSelectedStackRef.get();

	}

	@ModifyReturnValue(method = "getRespawnDimension", at = @At("RETURN"))
	private ResourceKey<Level> apoli$modifySpawnPointDimension(ResourceKey<Level> original) {

		if (!this.apoli$isEndRespawning() && (this.respawnPosition == null || this.apoli$hasObstructedOriginalSpawnPoint())) {
			return PowerHolderComponent.getPowerTypes(this, ModifyPlayerSpawnPowerType.class)
				.stream()
				.max(Comparator.comparing(ModifyPlayerSpawnPowerType::getPriority))
				.map(ModifyPlayerSpawnPowerType::getDimensionKey)
				.orElse(original);
		} else {
			return original;
		}

	}

	@ModifyReturnValue(method = "getRespawnPosition", at = @At("RETURN"))
	private BlockPos apoli$modifySpawnPointPosition(BlockPos original) {

		if (this.apoli$isEndRespawning() || !PowerHolderComponent.hasPowerType(this, ModifyPlayerSpawnPowerType.class)) {
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
		return original || (!this.apoli$isEndRespawning() && (respawnPosition == null || this.apoli$hasObstructedOriginalSpawnPoint()) && PowerHolderComponent.hasPowerType(this, ModifyPlayerSpawnPowerType.class));
	}

	@WrapOperation(method = "findRespawnPositionAndUseSpawnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findRespawnAndUseSpawnBlock(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;FZZ)Ljava/util/Optional;"))
	private Optional<ServerPlayer.RespawnPosAngle> apoli$retryObstructedSpawnPointIfFailed(ServerLevel world, BlockPos pos, float spawnAngle, boolean spawnForced, boolean alive, @NotNull Operation<Optional<ServerPlayer.RespawnPosAngle>> original) {

		Optional<ServerPlayer.RespawnPosAngle> originalRespawnPos = original.call(world, pos, spawnAngle, spawnForced, alive);

		if (originalRespawnPos.isEmpty() && PowerHolderComponent.hasPowerType(this, ModifyPlayerSpawnPowerType.class)) {
			return Optional
				.ofNullable(DismountHelper.findSafeDismountLocation(this.getType(), world, pos, spawnForced))
				.map(newPos -> ServerPlayer.RespawnPosAngle.of(newPos, pos, false, false));
		} else {
			return originalRespawnPos;
		}

	}

	@Unique
	private boolean apoli$hasObstructedOriginalSpawnPoint() {
		ServerLevel spawnPointWorld = this.server.getLevel(respawnDimension);
		return respawnDimension != null
			&& spawnPointWorld != null
			&& findRespawnAndUseSpawnBlock(spawnPointWorld, this.respawnPosition, this.respawnAngle, this.respawnForced, true).isEmpty();
	}

	@Unique
	private BlockPos apoli$findPowerSpawnPoint() {
		return PowerHolderComponent.getPowerTypes(this, ModifyPlayerSpawnPowerType.class)
			.stream()
			.max(Comparator.comparing(ModifyPlayerSpawnPowerType::getPriority))
			.flatMap(ModifyPlayerSpawnPowerType::getSpawn)
			.map(Tuple::getB)
			.orElse(null);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void apoli$phasing(CallbackInfo ci) {
		if (!PowerHolderComponent.hasPowerType(this, PhasingPowerType.class) && this.apoli$isPhasing()) {
			this.connection.send(PhasingPowerType.prepareResync((ServerPlayer) (Object) this));
			this.apoli$setPhasing(false);
			return;
		}

		CraftPlayer craftPlayer = this.getBukkitEntity();
		for (PhasingPowerType phasingPower : PowerHolderComponent.getPowerTypes(this, PhasingPowerType.class, true)) {
			if (phasingPower.isActive()) {

				if (phasingPower.shouldPhaseDown() && this.isShiftKeyDown()) {
					this.connection.send(PhasingPowerType.preparePacket((ServerPlayer) (Object) this));
					this.apoli$setPhasing(true);
					this.setDeltaMovement(this.getDeltaMovement().x, -0.1, this.getDeltaMovement().z);
				}

				Set<Block> toPhase = originspaper$getBlocksToPhase(craftPlayer, phasingPower);
				if (!toPhase.isEmpty()) {
					this.connection.send(PhasingPowerType.preparePacket((ServerPlayer) (Object) this));
					this.apoli$setPhasing(true);

					if (phasingPower.getRenderType().equals(PhasingPowerType.RenderType.BLINDNESS)) {
						craftPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 112, false, false, false));
					}

					if (this.getBukkitEntity().getAllowFlight()) {
						this.getBukkitEntity().setFlying(true);
					}

					craftPlayer.setFlySpeed(0.03F);
				} else {
					this.apoli$setPhasing(false);
					this.connection.send(PhasingPowerType.prepareResync((ServerPlayer) (Object) this));
					if (phasingPower.getRenderType().equals(PhasingPowerType.RenderType.BLINDNESS)) {
						craftPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
					}
					craftPlayer.setFlySpeed(0.1F);
				}
			} else {
				// Resync the client with the server information
				this.connection.send(PhasingPowerType.prepareResync((ServerPlayer) (Object) this));
				this.apoli$setPhasing(false);
				if (phasingPower.getRenderType().equals(PhasingPowerType.RenderType.BLINDNESS)) {
					craftPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
				}
				craftPlayer.setFlySpeed(0.1F);

			}
		}
	}

	@Unique
	private @NotNull Set<Block> originspaper$getBlocksToPhase(CraftPlayer player, PhasingPowerType powerType) {
		Set<Block> touchingBlocks = new HashSet<>();

		for (Vector offset : offsets) {
			Block blockAtFeet = player.getLocation().add(offset).getBlock();
			Block blockAtHead = player.getEyeLocation().add(offset).getBlock();
			BlockInWorld feet = new BlockInWorld(player.getHandle().level(), ((CraftBlock) blockAtFeet).getPosition(), false);
			BlockInWorld head = new BlockInWorld(player.getHandle().level(), ((CraftBlock) blockAtHead).getPosition(), false);
			if (feet.getState() == null || head.getState() == null) continue;
			boolean shouldPhaseFeet = powerType.shouldPhase(feet.getState().getCollisionShape(this.serverLevel(), ((CraftBlock) blockAtFeet).getPosition()), ((CraftBlock) blockAtFeet).getPosition());
			boolean shouldPhaseHead = powerType.shouldPhase(head.getState().getCollisionShape(this.serverLevel(), ((CraftBlock) blockAtHead).getPosition()), ((CraftBlock) blockAtHead).getPosition());

			if ((blockAtFeet.isCollidable() && blockAtFeet.isSolid()) && shouldPhaseFeet) {
				touchingBlocks.add(blockAtFeet);
			}

			if ((blockAtHead.isCollidable() && blockAtHead.isSolid()) && shouldPhaseHead) {
				touchingBlocks.add(blockAtHead);
			}
		}

		return touchingBlocks;
	}

	@WrapOperation(method = "getBedResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setRespawnPosition(Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/BlockPos;FZZLcom/destroystokyo/paper/event/player/PlayerSetSpawnEvent$Cause;)Z"))
	private boolean apoli$preventSleep(ServerPlayer serverPlayer, ResourceKey<Level> levelResourceKey, BlockPos position, float pos, boolean angle, boolean forced, PlayerSetSpawnEvent.Cause sendMessage, Operation<Boolean> original, @Cancellable CallbackInfoReturnable<Either<BedSleepingProblem, Unit>> cir) {

		List<PreventSleepPowerType> preventSleepPowers = PowerHolderComponent.getPowerTypes(this, PreventSleepPowerType.class)
			.stream()
			.filter(type -> type.doesPrevent(this.level(), position))
			.sorted(Comparator.comparing(PreventSleepPowerType::getPriority))
			.toList();

		if (preventSleepPowers.isEmpty()) {
			original.call(serverPlayer, levelResourceKey, position, pos, angle, forced, sendMessage);
		} else {

			if (preventSleepPowers.stream().allMatch(PreventSleepPowerType::doesAllowSpawnPoint)) {
				original.call(serverPlayer, levelResourceKey, position, pos, angle, forced, sendMessage);
			}

			cir.setReturnValue(Either.left(BedSleepingProblem.OTHER_PROBLEM));
			this.getBukkitEntity().sendMessage(PaperAdventure.asAdventure(preventSleepPowers.getLast().getMessage()));

		}

		return angle;
	}

}
