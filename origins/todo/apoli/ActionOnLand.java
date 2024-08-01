package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.Actions;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.GameEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.GenericGameEvent;
import org.jetbrains.annotations.NotNull;

public class ActionOnLand extends PowerType {
	private final FactoryJsonObject entityAction;

	public ActionOnLand(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction) {
		super(name, description, hidden, condition, loading_priority);
		this.entityAction = entityAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("action_on_land"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void e(@NotNull GenericGameEvent e) {
		if (e.getEvent() != GameEvent.HIT_GROUND) return;
		if (!(e.getEntity() instanceof Player player)) return;
		if (!getPlayers().contains(player)) return;
		if (!isActive(player)) return;
		Actions.executeEntity(player, entityAction);
	}

}
