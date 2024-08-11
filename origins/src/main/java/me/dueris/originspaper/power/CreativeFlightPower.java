package me.dueris.originspaper.power;

import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

// TODO
public class CreativeFlightPower extends PowerType {
	public CreativeFlightPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static InstanceDefiner buildDefiner() {
		return PowerType.buildDefiner().typedRegistry(OriginsPaper.apoliIdentifier("creative_flight"));
	}

	@Override
	public void tickAsync(@NotNull Player p) {
		GameType m = ((ServerPlayer) p).gameMode.getGameModeForPlayer();
		ResourceLocation insideBlock = OriginsPaper.identifier("insideblock");
		PersistentDataContainer container = p.getBukkitEntity().getPersistentDataContainer();
		if (Boolean.TRUE.equals(container.get(CraftNamespacedKey.fromMinecraft(insideBlock), PersistentDataType.BOOLEAN))) {
			if (((org.bukkit.entity.Player) p.getBukkitEntity()).getAllowFlight()) {
				((org.bukkit.entity.Player) p.getBukkitEntity()).setFlying(true);
			}
		} else {
			if (PowerHolderComponent.hasPowerType(p.getBukkitEntity(), CreativeFlightPower.class)) {
				((org.bukkit.entity.Player) p.getBukkitEntity()).setAllowFlight(isActive(p) || m.equals(GameMode.SPECTATOR) || m.equals(GameMode.CREATIVE));
			} else {
				boolean a = m.equals(GameMode.SPECTATOR) || m.equals(GameMode.CREATIVE) ||
					// TODO
					PowerHolderComponent.hasPowerType(p.getBukkitEntity(), ElytraFlightPower.class)/* || PowerHolderComponent.hasPowerType(p.getBukkitEntity(), GravityPower.class) ||
					PowerHolderComponent.hasPowerType(p.getBukkitEntity(), Grounded.class) || PowerHolderComponent.hasPowerType(p.getBukkitEntity(), Swimming.class) || PowerHolderComponent.isInPhantomForm(p.getBukkitEntity())*/;
				if (a && !((org.bukkit.entity.Player) p.getBukkitEntity()).getAllowFlight()) {
					((org.bukkit.entity.Player) p.getBukkitEntity()).setAllowFlight(true);
				} else if (!a && ((org.bukkit.entity.Player) p.getBukkitEntity()).getAllowFlight()) {
					((org.bukkit.entity.Player) p.getBukkitEntity()).setAllowFlight(false);
				}
			}
		}
		if (m.equals(GameMode.SPECTATOR)) {
			((org.bukkit.entity.Player) p.getBukkitEntity()).setFlying(true);
		}
		if (p.getBukkitEntity().getChunk().isLoaded()) {
			// TODO
			/* if (Phasing.inPhantomFormBlocks.contains(p)) {
				container.set(CraftNamespacedKey.fromMinecraft(insideBlock), PersistentDataType.BOOLEAN, true);
			} else {
				container.set(CraftNamespacedKey.fromMinecraft(insideBlock), PersistentDataType.BOOLEAN, false);
			} */
		}
	}
}
