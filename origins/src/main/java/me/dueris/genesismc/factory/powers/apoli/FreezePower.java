package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;

public class FreezePower extends PowerType {

	@Register
	public FreezePower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("freeze"));
	}

	@Override
	public void tick(Player p) {
		if (isActive(p)) {
			if (p.getFreezeTicks() >= 138) {
				p.setFreezeTicks(150);
			} else {
				p.setFreezeTicks(Math.min(p.getMaxFreezeTicks(), p.getFreezeTicks() + 3));
			}
		}
	}
}
