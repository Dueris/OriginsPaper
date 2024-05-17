package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ActionOnItemPickup extends PowerType {
	private final FactoryJsonObject itemCondition;
	private final FactoryJsonObject itemAction;

	public ActionOnItemPickup(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject itemCondition, FactoryJsonObject itemAction) {
		super(name, description, hidden, condition, loading_priority);
		this.itemCondition = itemCondition;
		this.itemAction = itemAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("action_on_item_pickup"))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void pickup(PlayerAttemptPickupItemEvent e) {
		Player p = e.getPlayer();
		if (this.getPlayers().contains(p)) {
			if (!ConditionExecutor.testItem(itemCondition, e.getItem().getItemStack()) || !isActive(p)) return;
			ItemStack clone = e.getItem().getItemStack().clone();
			Actions.executeItem(clone, itemAction);
			// Needs to update the ItemEntity
			e.getItem().setItemStack(clone);
		}
	}

}
