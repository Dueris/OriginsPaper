package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Swimming extends CraftPower {


	@Override
	public void run(Player p) {
		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			if (swimming.contains(p)) {
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					if (!ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
						setActive(p, power.getTag(), false);
						return;
					} else {
						p.setSwimming(true);
						setActive(p, power.getTag(), true);
					}
				}
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:swimming";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return swimming;
	}
}
