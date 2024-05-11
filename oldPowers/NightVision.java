package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class NightVision extends CraftPower {

	@Override
	public void run(Player p, Power power) {
		if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
			setActive(p, power.getTag(), true);
			p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, roundNumber(power.getNumberOrDefault("strength", 1.0f).getFloat()), false, false, false));
		} else {
			p.removePotionEffect(PotionEffectType.NIGHT_VISION);
			setActive(p, power.getTag(), false);
		}
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
	public String getType() {
		return "apoli:night_vision";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return night_vision;
	}
}
