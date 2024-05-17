package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PreventBlockPlace extends PowerType implements Listener {
	private final FactoryJsonObject itemCondition;
	private final FactoryJsonObject placeToCondition;
	private final FactoryJsonObject placeOnCondition;
	private final FactoryJsonObject entityAction;
	private final FactoryJsonObject heldItemAction;
	private final FactoryJsonObject placeOnAction;
	private final FactoryJsonObject placeToAction;

	public PreventBlockPlace(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject itemCondition, FactoryJsonObject placeToCondition, FactoryJsonObject placeOnCondition, FactoryJsonObject entityAction, FactoryJsonObject heldItemAction, FactoryJsonObject placeOnAction, FactoryJsonObject placeToAction) {
		super(name, description, hidden, condition, loading_priority);
		this.itemCondition = itemCondition;
		this.placeToCondition = placeToCondition;
		this.placeOnCondition = placeOnCondition;
		this.entityAction = entityAction;
		this.heldItemAction = heldItemAction;
		this.placeOnAction = placeOnAction;
		this.placeToAction = placeToAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("prevent_block_place"))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("place_to_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("place_on_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("held_item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("place_on_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("place_to_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void blockBreak(BlockPlaceEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			if (!(isActive(e.getPlayer()) && ConditionExecutor.testItem(itemCondition, e.getItemInHand()) &&
				ConditionExecutor.testBlock(placeToCondition, (CraftBlock) e.getBlockPlaced()) && ConditionExecutor.testBlock(placeOnCondition, (CraftBlock) e.getBlockAgainst()))) return;
			e.setCancelled(true);
			Actions.executeEntity(e.getPlayer(), entityAction);
			Actions.executeItem(e.getItemInHand(), heldItemAction);
			Actions.executeBlock(e.getBlockAgainst().getLocation(), placeOnAction);
			Actions.executeBlock(e.getBlockPlaced().getLocation(), placeToAction);
		}
	}

}
