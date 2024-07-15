package me.dueris.originspaper.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import me.dueris.originspaper.factory.powers.originspaper.GravityPower;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.minecraft.resources.ResourceLocation;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class CreativeFlight extends PowerType {

	public CreativeFlight(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("creative_flight"));
	}

	@Override
	public void tickAsync(Player p) {
		GameMode m = p.getGameMode();
		ResourceLocation insideBlock = OriginsPaper.identifier("insideblock");
		PersistentDataContainer container = p.getPersistentDataContainer();
		if (Boolean.TRUE.equals(container.get(CraftNamespacedKey.fromMinecraft(insideBlock), PersistentDataType.BOOLEAN))) {
			if (p.getAllowFlight()) {
				p.setFlying(true);
			}
		} else {
			if (PowerHolderComponent.hasPowerType(p, CreativeFlight.class)) {
				p.setAllowFlight(ConditionExecutor.testEntity(getCondition(), p) || m.equals(GameMode.SPECTATOR) || m.equals(GameMode.CREATIVE));
			} else {
				boolean a = m.equals(GameMode.SPECTATOR) || m.equals(GameMode.CREATIVE) ||
					PowerHolderComponent.hasPowerType(p, ElytraFlightPower.class) || PowerHolderComponent.hasPowerType(p, GravityPower.class) ||
					PowerHolderComponent.hasPowerType(p, Grounded.class) || PowerHolderComponent.hasPowerType(p, Swimming.class) || PowerHolderComponent.isInPhantomForm(p);
				if (a && !p.getAllowFlight()) {
					p.setAllowFlight(true);
				} else if (!a && p.getAllowFlight()) {
					p.setAllowFlight(false);
				}
			}
		}
		if (m.equals(GameMode.SPECTATOR)) {
			p.setFlying(true);
		}
		if (p.getChunk().isLoaded()) {
			if (Phasing.inPhantomFormBlocks.contains(p)) {
				container.set(CraftNamespacedKey.fromMinecraft(insideBlock), PersistentDataType.BOOLEAN, true);
			} else {
				container.set(CraftNamespacedKey.fromMinecraft(insideBlock), PersistentDataType.BOOLEAN, false);
			}
		}
	}
}
