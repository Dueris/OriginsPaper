package me.dueris.originspaper.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;

public class FreezePower extends PowerType {

	public FreezePower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("freeze"));
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
