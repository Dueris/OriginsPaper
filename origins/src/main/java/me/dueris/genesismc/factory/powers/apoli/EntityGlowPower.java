package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.FactoryObjectInstance;
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
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
						/**
						 * Will not take entity_conditions due to them being evaluated on the clientside for this power
						 */
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

	@Override
	public List<FactoryObjectInstance> getValidObjectFactory() {
		return super.getDefaultObjectFactory(List.of(
			new FactoryObjectInstance("red", Float.class, 1.0f),
			new FactoryObjectInstance("blue", Float.class, 1.0f),
			new FactoryObjectInstance("green", Float.class, 1.0f),
			new FactoryObjectInstance("bientity_condition", JSONObject.class, new JSONObject())
		));
	}
}
