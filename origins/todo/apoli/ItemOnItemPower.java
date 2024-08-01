package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.action.Actions;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemOnItemPower extends PowerType {

	private final FactoryJsonObject usingItemCondition;
	private final FactoryJsonObject onItemCondition;
	private final ItemStack resultStack;
	private final FactoryJsonObject resultItemAction;
	private final int resultFromOnStack;
	private final FactoryJsonObject usingItemAction;
	private final FactoryJsonObject onItemAction;
	private final FactoryJsonObject entityAction;

	public ItemOnItemPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject usingItemCondition,
						   FactoryJsonObject onItemCondition, ItemStack resultStack, FactoryJsonObject resultItemAction, int resultFromOnStack,
						   FactoryJsonObject usingItemAction, FactoryJsonObject onItemAction, FactoryJsonObject entityAction) {
		super(name, description, hidden, condition, loading_priority);
		this.usingItemCondition = usingItemCondition;
		this.onItemCondition = onItemCondition;
		this.resultStack = resultStack;
		this.resultItemAction = resultItemAction;
		this.resultFromOnStack = resultFromOnStack;
		this.usingItemAction = usingItemAction;
		this.onItemAction = onItemAction;
		this.entityAction = entityAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("item_on_item"))
			.add("using_item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("on_item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("result", ItemStack.class, new OptionalInstance())
			.add("result_item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("result_from_on_stack", int.class, 1)
			.add("using_item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("on_item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void itemOnItem(@NotNull InventoryClickEvent e) {
		if (e.getCursor() != null && e.getCurrentItem() != null) { // Valid event
			Player p = (Player) e.getWhoClicked();
			if (p.getGameMode().equals(GameMode.CREATIVE)) return;
			if (this.getPlayers().contains(p)) {
				boolean pass =
					ConditionExecutor.testItem(usingItemCondition, e.getCursor()) &&
						ConditionExecutor.testItem(onItemCondition, e.getCurrentItem());
				if (pass) {
					if (resultStack != null) {
						Actions.executeItem(resultStack, p.getWorld(), resultItemAction);
						for (int i = 0; i < resultFromOnStack; i++) {
							p.getInventory().addItem(resultStack);
						}
					}
					Actions.executeItem(e.getCursor(), p.getWorld(), usingItemAction);
					Actions.executeItem(e.getCurrentItem(), p.getWorld(), onItemAction);
					Actions.executeEntity(e.getWhoClicked(), entityAction);
				}
			}
		}
	}

}
