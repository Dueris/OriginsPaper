package io.github.dueris.originspaper.power;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreativeFlightPower extends PowerType {
	public CreativeFlightPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("creative_flight"));
	}

	public static void tickPlayer(@NotNull Player p, @Nullable CreativeFlightPower power) {
		GameType m = ((ServerPlayer) p).gameMode.getGameModeForPlayer();
		ResourceLocation insideBlock = OriginsPaper.identifier("insideblock");
		PersistentDataContainer container = p.getBukkitEntity().getPersistentDataContainer();
		if (Boolean.TRUE.equals(container.get(CraftNamespacedKey.fromMinecraft(insideBlock), PersistentDataType.BOOLEAN))) {
			if (((org.bukkit.entity.Player) p.getBukkitEntity()).getAllowFlight()) {
				((org.bukkit.entity.Player) p.getBukkitEntity()).setFlying(true);
			}
		} else {
			if (PowerHolderComponent.hasPowerType(p.getBukkitEntity(), CreativeFlightPower.class)) {
				((org.bukkit.entity.Player) p.getBukkitEntity()).setAllowFlight((power == null || power.isActive(p)) || m.equals(GameType.SPECTATOR) || m.equals(GameType.CREATIVE));
			} else {
				boolean a = m.equals(GameType.SPECTATOR) || m.equals(GameType.CREATIVE) ||
					PowerHolderComponent.hasPowerType(p.getBukkitEntity(), ElytraFlightPower.class) ||
					PowerHolderComponent.hasPowerType(p.getBukkitEntity(), GroundedPower.class) ||
					isInPhantomForm((org.bukkit.entity.Player) p.getBukkitEntity());
				if (a && !((org.bukkit.entity.Player) p.getBukkitEntity()).getAllowFlight()) {
					((org.bukkit.entity.Player) p.getBukkitEntity()).setAllowFlight(true);
				} else if (!a && ((org.bukkit.entity.Player) p.getBukkitEntity()).getAllowFlight()) {
					((org.bukkit.entity.Player) p.getBukkitEntity()).setAllowFlight(false);
				}
			}
		}
		if (m.equals(GameType.SPECTATOR)) {
			((org.bukkit.entity.Player) p.getBukkitEntity()).setFlying(true);
		}
		if (p.getBukkitEntity().getChunk().isLoaded()) {
			if (PhasingPower.PHASING_BLOCKS.contains(p)) {
				container.set(CraftNamespacedKey.fromMinecraft(insideBlock), PersistentDataType.BOOLEAN, true);
			} else {
				container.set(CraftNamespacedKey.fromMinecraft(insideBlock), PersistentDataType.BOOLEAN, false);
			}
		}
	}

	public static boolean isInPhantomForm(@NotNull org.bukkit.entity.Player player) {
		return player.getPersistentDataContainer().has(CraftNamespacedKey.fromString("originspaper:in-phantomform"))
			? player.getPersistentDataContainer().get(CraftNamespacedKey.fromString("originspaper:in-phantomform"), PersistentDataType.BOOLEAN)
			: false;
	}

	@Override
	public void tickAsync(Player player) {
		tickPlayer(player, this);
	}
}
