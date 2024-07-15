package me.dueris.originspaper.factory.powers.apoli;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.actions.Actions;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.powers.holder.PowerType;
import net.minecraft.world.InteractionHand;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ActionOnBlockPlace extends PowerType {

	private final FactoryJsonObject entityAction;
	private final FactoryJsonObject heldItemAction;
	private final FactoryJsonObject placeToAction;
	private final FactoryJsonObject placeOnAction;
	private final FactoryJsonObject itemCondition;
	private final FactoryJsonObject placeToCondition;
	private final FactoryJsonObject placeOnCondition;
	private final List<BlockFace> directions;
	private final FactoryJsonArray hands;
	private final ItemStack resultStack;
	private final FactoryJsonObject resultItemAction;

	public ActionOnBlockPlace(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction, FactoryJsonObject heldItemAction, FactoryJsonObject placeToAction, FactoryJsonObject placeOnAction, FactoryJsonObject itemCondition, FactoryJsonObject placeToCondition, FactoryJsonObject placeOnCondition, FactoryJsonArray directions, FactoryJsonArray hands, ItemStack resultStack, FactoryJsonObject resultItemAction) {
		super(name, description, hidden, condition, loading_priority);
		this.entityAction = entityAction;
		this.heldItemAction = heldItemAction;
		this.placeToAction = placeToAction;
		this.placeOnAction = placeOnAction;
		this.itemCondition = itemCondition;
		this.placeToCondition = placeToCondition;
		this.placeOnCondition = placeOnCondition;
		this.directions = directions.asList().stream().map(FactoryElement::getString).map(String::toUpperCase).map(BlockFace::valueOf).toList();
		this.hands = hands;
		this.resultStack = resultStack;
		this.resultItemAction = resultItemAction;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(OriginsPaper.apoliIdentifier("action_on_block_place"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("held_item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("place_to_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("place_on_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("place_to_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("place_on_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("directions", FactoryJsonArray.class, new FactoryJsonArray(new Gson().fromJson("[\"up\", \"down\", \"north\", \"south\", \"east\", \"west\"]", JsonArray.class)))
			.add("hands", FactoryJsonArray.class, new FactoryJsonArray(new Gson().fromJson("[\"off_hand\", \"main_hand\"]", JsonArray.class)))
			.add("result_stack", ItemStack.class, new OptionalInstance())
			.add("result_item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			new BukkitRunnable() {
				@Override
				public void run() {
					if (e.isCancelled()) return;
					if (!isActive(e.getPlayer())) return;
					if (!ConditionExecutor.testItem(itemCondition, e.getItemInHand())) return;
					if (!ConditionExecutor.testBlock(placeToCondition, e.getBlockPlaced())) return;
					if (!ConditionExecutor.testBlock(placeOnCondition, e.getBlockAgainst())) return;
					boolean pass = true;
					if (e.getHand().isHand()) {
						InteractionHand hand = CraftEquipmentSlot.getHand(e.getHand());
						pass = hands.asList().stream().map(FactoryElement::getString).map(String::toUpperCase).map(InteractionHand::valueOf).toList().contains(hand);
					}
					BlockFace direction = e.getPlayer().getTargetBlockFace(6);
					if (!directions.contains(direction)) return;
					if (!pass) return;
					Actions.executeEntity(e.getPlayer(), entityAction);
					Actions.executeItem(e.getItemInHand(), e.getPlayer().getWorld(), heldItemAction);
					Actions.executeBlock(e.getBlockAgainst().getLocation(), placeOnAction);
					Actions.executeBlock(e.getBlockPlaced().getLocation(), placeToAction);
					if (resultStack != null) {
						Actions.executeItem(resultStack, e.getPlayer().getWorld(), resultItemAction);
						e.getPlayer().getInventory().addItem(resultStack);
					}
				}
			}.runTaskLater(OriginsPaper.getPlugin(), 2);
		}
	}

	public FactoryJsonObject getEntityAction() {
		return entityAction;
	}

	public FactoryJsonObject getHeldItemAction() {
		return heldItemAction;
	}

	public FactoryJsonObject getPlaceToAction() {
		return placeToAction;
	}

	public FactoryJsonObject getPlaceOnAction() {
		return placeOnAction;
	}

	public List<BlockFace> getDirections() {
		return directions;
	}

	public FactoryJsonArray getHands() {
		return hands;
	}

	public ItemStack getResultStack() {
		return resultStack;
	}

	public FactoryJsonObject getResultItemAction() {
		return resultItemAction;
	}
}
