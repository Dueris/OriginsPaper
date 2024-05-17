package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryElement;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import net.minecraft.world.InteractionHand;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ActionOnBlockUse extends PowerType {
	public static ArrayList<Player> tickFix = new ArrayList<>();
	private final FactoryJsonObject entityAction;
	private final FactoryJsonObject blockAction;
	private final FactoryJsonObject blockCondition;
	private final FactoryJsonObject itemCondition;
	private final ItemStack resultStack;
	private final FactoryJsonObject resultItemAction;
	private final FactoryJsonObject heldItemAction;
	private final List<BlockFace> directions;
	private final FactoryJsonArray hands;

	public ActionOnBlockUse(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityAction, FactoryJsonObject blockAction, FactoryJsonObject blockCondition, FactoryJsonObject itemCondition, FactoryJsonArray directions, FactoryJsonArray hands, ItemStack resultStack, FactoryJsonObject resultItemAction, FactoryJsonObject heldItemAction) {
		super(name, description, hidden, condition, loading_priority);
		this.entityAction = entityAction;
		this.blockAction = blockAction;
		this.blockCondition = blockCondition;
		this.itemCondition = itemCondition;
		this.resultStack = resultStack;
		this.resultItemAction = resultItemAction;
		this.heldItemAction = heldItemAction;
		this.directions = directions.asList().stream().map(FactoryElement::getString).map(String::toUpperCase).map(BlockFace::valueOf).toList();
		this.hands = hands;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("action_on_block_use"))
			.add("entity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("block_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("block_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("directions", FactoryJsonArray.class, new FactoryJsonArray(new Gson().fromJson("[\"up\", \"down\", \"north\", \"south\", \"east\", \"west\"]", JsonArray.class)))
			.add("hands", FactoryJsonArray.class, new FactoryJsonArray(new Gson().fromJson("[\"off_hand\", \"main_hand\"]", JsonArray.class)))
			.add("result_stack", ItemStack.class, new OptionalInstance())
			.add("result_item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("held_item_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));
	}

	@EventHandler
	public void execute(PlayerInteractEvent e) {
		if (e.getClickedBlock() == null) return;
		if (e.getAction().isLeftClick() || e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
		Player actor = e.getPlayer();

		if (!getPlayers().contains(actor)) return;

		if (isActive(actor) && ConditionExecutor.testBlock(blockCondition, e.getClickedBlock()) && ConditionExecutor.testItem(itemCondition, e.getItem())) {
			boolean pass = false;
			if (e.getHand().isHand()) {
				InteractionHand hand = CraftEquipmentSlot.getHand(e.getHand());
				pass = hands.asList().stream().map(FactoryElement::getString).map(String::toUpperCase).map(InteractionHand::valueOf).toList().contains(hand);
			}
			BlockFace direction = e.getBlockFace();
			if (!directions.contains(direction)) return;
			if (!pass) return;
			Actions.executeBlock(e.getClickedBlock().getLocation(), blockAction);
			Actions.executeEntity(actor, entityAction);
			Actions.executeItem(e.getItem(), heldItemAction);
			if (resultStack != null) {
				Actions.executeItem(resultStack, resultItemAction);
				actor.getInventory().addItem(resultStack);
			}
		}
	}

	public List<BlockFace> getDirections() {
		return directions;
	}

	public FactoryJsonArray getHands() {
		return hands;
	}
}
