package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class NightVision extends CraftPower {

	public NightVision() {

	}


	@Override
	public void run(Player p) {
		// HashMap<LayerContainer, OriginContainer> origins = OriginPlayerUtils.getOrigin(p);
		// Set<LayerContainer> layers = origins.keySet();
		// for (LayerContainer layer : layers) {
		if (night_vision.contains(p)) {
			for (Layer layer : CraftApoli.getLayersFromRegistry()) {
				for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
					if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p)) {
						setActive(p, power.getTag(), true);
						p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, roundNumber(power.getFloatOrDefault("strength", 1.0f)), false, false, false));
					} else {
						setActive(p, power.getTag(), false);
					}
				}
			}
		}

		// }
	}

	public int roundNumber(double num) {
		if (String.valueOf(num).contains(".")) {
			String[] parts = String.valueOf(num).split("\\.");
			if (parts.length > 1) {
				int decimalPart = Integer.parseInt(parts[1]);
				if (decimalPart >= 5) {
					return Integer.parseInt(parts[0]) + 1;
				} else {
					return Integer.parseInt(parts[0]);
				}
			}
		}
		return 0;
	}


	@Override
	public String getPowerFile() {
		return "apoli:night_vision";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return night_vision;
	}
}
