package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

public class PreventEntityUse extends PowerType implements Listener {

	private final FactoryJsonObject bientityCondition;
	private final FactoryJsonObject itemCondition;

	public PreventEntityUse(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject bientityCondition, FactoryJsonObject itemCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.bientityCondition = bientityCondition;
		this.itemCondition = itemCondition;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("prevent_entity_use"))
			.add("bientity_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void OnClickREACH(@NotNull PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if (isActive(p) && ConditionExecutor.testBiEntity(bientityCondition, p, e.getRightClicked()) && ConditionExecutor.testItem(itemCondition, e.getPlayer().getInventory().getItem(e.getHand()))) {
			e.setCancelled(true);
		}
	}

}
