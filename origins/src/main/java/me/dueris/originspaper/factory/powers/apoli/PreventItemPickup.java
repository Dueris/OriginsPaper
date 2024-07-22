package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.jetbrains.annotations.NotNull;

public class PreventItemPickup extends PowerType implements Listener {
	private final FactoryJsonObject itemCondition;
	private final FactoryJsonObject itemAction;
	private final FactoryJsonObject bientityActionItem;

	public PreventItemPickup(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject itemCondition, FactoryJsonObject itemAction, FactoryJsonObject bientityActionItem) {
		super(name, description, hidden, condition, loading_priority);
		this.itemCondition = itemCondition;
		this.itemAction = itemAction;
		this.bientityActionItem = bientityActionItem;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("prevent_item_pickup"))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("bientity_action_item", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void pickup(@NotNull PlayerAttemptPickupItemEvent e) {
		Player p = e.getPlayer();
		if (this.getPlayers().contains(p)) {
			boolean shouldCancel = ConditionExecutor.testItem(itemCondition, e.getItem().getItemStack());
			if (shouldCancel) e.setCancelled(true);
			Actions.executeItem(e.getItem().getItemStack(), e.getPlayer().getWorld(), itemAction);
			Actions.executeBiEntity(p, e.getItem(), bientityActionItem);
		}
	}

}
