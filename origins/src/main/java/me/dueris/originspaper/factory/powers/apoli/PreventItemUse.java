package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PreventItemUse extends PowerType implements Listener {

	private final FactoryJsonObject itemCondition;

	public PreventItemUse(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject itemCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.itemCondition = itemCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("prevent_item_use"))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void runD(PlayerInteractEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			if (e.getItem() == null) return;
			boolean shouldCancel = ConditionExecutor.testItem(itemCondition, e.getItem());
			if (shouldCancel) e.setCancelled(true);
		}
	}

}
