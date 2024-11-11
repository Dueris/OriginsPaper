package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.dueris.originspaper.access.EndRespawningEntity;
import io.github.dueris.originspaper.component.PowerHolderComponent;
import io.github.dueris.originspaper.power.type.ActionOnItemUsePowerType;
import io.github.dueris.originspaper.power.type.GroundedPowerType;
import io.github.dueris.originspaper.power.type.ModifyCraftingPowerType;
import io.github.dueris.originspaper.power.type.PhasingPowerType;
import io.github.dueris.originspaper.util.InventoryUtil;
import io.github.dueris.originspaper.util.PriorityPhase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Location;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

	@Shadow
	public ServerPlayer player;

	@Shadow
	protected static double clampHorizontal(double d) {
		return 0;
	}

	@Shadow
	protected static double clampVertical(double d) {
		return 0;
	}

	@Inject(method = "handleSetCarriedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ServerboundSetCarriedItemPacket;getSlot()I", ordinal = 0))
	private void callActionOnUseStopBySwitching(ServerboundSetCarriedItemPacket packet, CallbackInfo ci) {
		if (player.isUsingItem()) {
			ActionOnItemUsePowerType.executeActions(player, SlotAccess.forContainer(player.getInventory(), this.player.getInventory().selected), player.getUseItem(), ActionOnItemUsePowerType.TriggerType.STOP, PriorityPhase.ALL);
		}
	}

	@Inject(method = "handlePlayerAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;stopUsingItem()V"))
	private void callActionOnUseStopBySwappingHands(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
		if (player.isUsingItem()) {
			ActionOnItemUsePowerType.executeActions(player, SlotAccess.forContainer(player.getInventory(), this.player.getInventory().selected), player.getUseItem(), ActionOnItemUsePowerType.TriggerType.STOP, PriorityPhase.ALL);
		}
	}

	@ModifyExpressionValue(method = "handlePlayerAbilities", at = @At(value = "INVOKE", target = "Lorg/bukkit/event/player/PlayerToggleFlightEvent;isCancelled()Z"))
	private boolean apoli$grounded(boolean original) {
		PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
		if (component != null) {
			boolean overwrite = false;
			for (GroundedPowerType powerType : component.getPowerTypes(GroundedPowerType.class)) {
				overwrite = powerType.isActive();
			}
			if (overwrite) {
				GroundedPowerType.action(this.player);
				return true;
			}
		}
		return original;
	}

	@WrapOperation(method = "handleContainerClick", at = @At(value = "INVOKE", target = "Lorg/bukkit/plugin/PluginManager;callEvent(Lorg/bukkit/event/Event;)V"))
	public void apoli$executeModifyCraftingActions(PluginManager instance, Event event, Operation<Void> original) {
		if (event instanceof CraftItemEvent craftItemEvent && craftItemEvent.getRecipe() instanceof CraftingRecipe craftingRecipe) {
			PowerHolderComponent component = PowerHolderComponent.KEY.getNullable(player);
			ResourceLocation location = CraftNamespacedKey.toMinecraft(craftingRecipe.getKey());
			if (component == null) {
				original.call(instance, event);
				return;
			}

			component.getPowerTypes(ModifyCraftingPowerType.class).stream().filter(p -> p.doesApply(location, null)).forEach(p -> {
				if (p.getEntityAction().isPresent()) {
					p.getEntityAction().get().execute(this.player);
				}
				if (p.getBlockAction().isPresent()) {
					p.getBlockAction().get().execute(
						this.player.level(), craftItemEvent.getInventory().getLocation() != null ? CraftLocation.toBlockPosition(craftItemEvent.getInventory().getLocation()) : this.player.blockPosition(), Optional.of(Direction.UP)
					);
				}
				if (p.getItemActionAfterCrafting().isPresent()) {
					ItemStack nmsResult = CraftItemStack.unwrap(craftItemEvent.getInventory().getResult());
					SlotAccess slotAccess = InventoryUtil.createStackReference(nmsResult);
					p.getItemActionAfterCrafting().get().execute(this.player.level(), slotAccess);
					craftItemEvent.getInventory().setResult(slotAccess.get().getBukkitStack());
				}
			});
		}
		original.call(instance, event);
	}

	@Inject(method = "handleClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;respawn(Lnet/minecraft/server/level/ServerPlayer;ZLnet/minecraft/world/entity/Entity$RemovalReason;Lorg/bukkit/event/player/PlayerRespawnEvent$RespawnReason;)Lnet/minecraft/server/level/ServerPlayer;", ordinal = 0))
	private void saveEndRespawnStatus(ServerboundClientCommandPacket packet, CallbackInfo ci) {
		((EndRespawningEntity) this.player).apoli$setEndRespawning(true);
	}

	@Inject(method = "handleClientCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/ChangeDimensionTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/ResourceKey;)V"))
	private void undoEndRespawnStatus(ServerboundClientCommandPacket packet, CallbackInfo ci) {
		((EndRespawningEntity) this.player).apoli$setEndRespawning(false);
	}

	@Inject(method = "handleMovePlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isChangingDimension()Z", shift = At.Shift.BEFORE, ordinal = 1))
	private void apoli$blockMovementCollision(@NotNull ServerboundMovePlayerPacket packet, CallbackInfo ci) {
		final double toX = clampHorizontal(packet.getX(this.player.getX()));
		final double toY = clampVertical(packet.getY(this.player.getY()));
		final double toZ = clampHorizontal(packet.getZ(this.player.getZ()));
		Location location = new Location(this.player.level().getWorld(), toX, toY, toZ);
		BlockPos pos = CraftLocation.toBlockPosition(location);
		BlockState state = player.level().getBlockState(pos);
		VoxelShape shape = state.getCollisionShape(this.player.level(), pos);
		for (PhasingPowerType phasingPower : PowerHolderComponent.getPowerTypes(this.player, PhasingPowerType.class)) {
			if (!phasingPower.shouldPhase(shape, pos) && !shape.isEmpty()) {
				this.player.noPhysics = false;
				break;
			}
		}
	}

}
