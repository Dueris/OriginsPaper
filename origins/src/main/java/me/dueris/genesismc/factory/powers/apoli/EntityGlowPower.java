package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class EntityGlowPower extends CraftPower {


	public Collection<Entity> getEntitiesInRadius(Player player, int radius) {
		Collection<Entity> entitiesInRadius = new HashSet<>();
		for (Entity entity : player.getLocation().getWorld().getEntities()) {
			if (entity.getLocation().distance(player.getLocation()) <= radius) {
				entitiesInRadius.add(entity);
			}
		}
		return entitiesInRadius;
	}

	@Override
	public void run(Player p) {
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			if (entity_glow.contains(p)) {
				Collection<Entity> entitiesWithinRadius = getEntitiesInRadius(p, 10);
				for (Entity entity : entitiesWithinRadius) {
					for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
						if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p) && ConditionExecutor.testBiEntity(power.get("bientity_condition"), (CraftEntity) p, (CraftEntity) entity)) {
							if (!entity.isGlowing()) {
								entity.setGlowing(true);
							}
							setActive(p, power.getTag(), true);
						} else {
							if (entity.isGlowing()) {
								entity.setGlowing(false);
							}
							setActive(p, power.getTag(), false);
						}
					}
				}
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:entity_glow";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return entity_glow;
	}
}
