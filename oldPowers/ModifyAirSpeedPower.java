package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ModifyAirSpeedPower extends CraftPower {
	private static final HashMap<Player, Float> base = new HashMap<>();

	public static void compute(Player p, Power power) {
		float b = base.containsKey(p) ? base.get(p) : p.getFlySpeed();
		power.getModifiers().forEach(modifier -> {
			float f = Utils.getOperationMappingsFloat().get(modifier.operation()).apply(b, modifier.value());
			if (f < 0) f = 0;
			if (f > 1) f = 1;
			p.setFlySpeed(f);
		});
		base.put(p, b);
	}

	@Override
	public void run(Player p, Power power) {
		if (Bukkit.getCurrentTick() % 10 == 0) {
			if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
				setActive(p, power.getTag(), false);
				p.setFlySpeed(base.get(p));
				return;
			}
			setActive(p, power.getTag(), true);
			compute(p, power);
		}
	}

	@Override
	public void doesntHavePower(Player p) {
		if (base.containsKey(p)) {
			p.setFlySpeed(base.get(p));
			base.remove(p);
		}
	}

	@Override
	public String getType() {
		return "apoli:modify_air_speed";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return modify_air_speed;
	}
}
