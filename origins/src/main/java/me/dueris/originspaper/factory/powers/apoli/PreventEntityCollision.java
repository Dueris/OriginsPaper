package me.dueris.originspaper.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.event.PowerUpdateEvent;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class PreventEntityCollision extends PowerType {

	public PreventEntityCollision(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("prevent_entity_collision"));
	}

	@Override
	public void tick(Player p) {
		p.setCollidable(isActive(p));
	}

	@EventHandler
	public void update(PowerUpdateEvent e) {
		e.getPlayer().setCollidable(true);
	}

}
