package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashMap;

public class ModifyAirSpeedPower extends ModifierPower {
	private static final HashMap<Player, Float> base = new HashMap<>();

	public ModifyAirSpeedPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("modify_air_speed"));
	}

	public void compute(Player p) {
		float b = base.containsKey(p) ? base.get(p) : p.getFlySpeed();
		for (Modifier modifier : getModifiers()) {
			float f = Utils.getOperationMappingsFloat().get(modifier.operation()).apply(b, modifier.value());
			if (f < 0) f = 0;
			if (f > 1) f = 1;
			p.setFlySpeed(f);
		}
		base.put(p, b);
	}

	@Override
	public void tick(Player p) {
		if (Bukkit.getCurrentTick() % 10 == 0) {
			if (!isActive(p)) {
				p.setFlySpeed(base.get(p));
				return;
			}
			compute(p);
		}
	}

	@EventHandler
	public void update(PowerUpdateEvent e) {
		Player p = e.getPlayer();
		if (base.containsKey(p)) {
			p.setFlySpeed(base.get(p));
			base.remove(p);
		}
	}

}
