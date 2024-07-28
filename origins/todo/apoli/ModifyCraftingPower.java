package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModifyCraftingPower extends PowerType implements Listener {
	private final String recipe;
	private final FactoryJsonObject itemAction;
	private final FactoryJsonObject entityAction;
	private final FactoryJsonObject blockAction;
	private final FactoryJsonObject itemCondition;
	private final ItemStack result;

	public ModifyCraftingPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, String recipe, FactoryJsonObject itemAction, FactoryJsonObject entityAction, FactoryJsonObject blockAction, FactoryJsonObject itemCondition, ItemStack result) {
		super(name, description, hidden, condition, loading_priority);
		this.recipe = recipe;
		this.itemAction = itemAction;
		this.entityAction = entityAction;
		this.blockAction = blockAction;
		this.itemCondition = itemCondition;
		this.result = result;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("modify_crafting"))
			.add("recipe", String.class, new OptionalInstance())
			.add("item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("block_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("result", ItemStack.class, new RequiredInstance());
	}

	@EventHandler
	public void runD(@NotNull PrepareItemCraftEvent e) {
		Player p = (Player) e.getInventory().getHolder();
		if (getPlayers().contains(p)) {
			if (e.getRecipe() == null) return;
			if (e.getInventory().getResult() == null) return;
			if (isActive(p)) {
				String currKey = RecipePower.computeTag(e.getRecipe());
				if (currKey == null) return;
				boolean set = false;
				if (currKey.equals(recipe)) { // Matched on crafting
					set = ConditionExecutor.testItem(itemCondition, e.getInventory().getResult());
				}
				if (set || recipe == null) {
					if (result != null) {
						e.getInventory().setResult(result);
					}
					Actions.executeEntity(p, entityAction);
					Actions.executeItem(e.getInventory().getResult(), p.getWorld(), itemAction);
					Actions.executeBlock(e.getInventory().getLocation(), blockAction);
				}
			}
		}
	}

	public FactoryJsonObject getBlockAction() {
		return blockAction;
	}

	public FactoryJsonObject getEntityAction() {
		return entityAction;
	}

	public FactoryJsonObject getItemAction() {
		return itemAction;
	}

	public FactoryJsonObject getItemCondition() {
		return itemCondition;
	}

	public ItemStack getResult() {
		return result;
	}

	public String getRecipe() {
		return recipe;
	}
}
