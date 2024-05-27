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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class ActionOnItemUse extends PowerType {
	private final FactoryJsonObject itemCondition;
	private final FactoryJsonObject entityAction;
	private final FactoryJsonObject itemAction;

	public ActionOnItemUse(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction, FactoryJsonObject itemAction, FactoryJsonObject itemCondition) {
		super(name, description, hidden, condition, loading_priority);
		this.itemCondition = itemCondition;
		this.entityAction = entityAction;
		this.itemAction = itemAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("action_on_item_use"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void entityRightClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if (!getPlayers().contains(player) || !e.getAction().isRightClick() || e.getItem() == null || !e.getHand().equals(EquipmentSlot.HAND)) return;
		if (!isActive(player)) return;
		if (!ConditionExecutor.testItem(itemCondition, e.getItem())) return;
		Actions.executeItem(e.getItem(), itemAction);
		Actions.executeEntity(player, entityAction);
	}

}
