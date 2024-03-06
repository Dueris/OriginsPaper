package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.calio.util.InstanceGetter;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActionOnBlockUse extends CraftPower implements Listener {

	public static ArrayList<Player> tickFix = new ArrayList<>();

	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void execute(PlayerInteractEvent e) {
		if (e.getClickedBlock() == null) return;
		if (e.getAction().isLeftClick() || e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
		if (tickFix.contains(e.getPlayer())) return;
		Player actor = e.getPlayer();

		if (!getPowerArray().contains(actor)) return;

		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(actor, getPowerFile(), layer)) {
				if (power == null) continue;
				if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) e.getPlayer()) &&
					ConditionExecutor.testBlock(power.get("block_condition"), (CraftBlock) e.getClickedBlock()) &&
					ConditionExecutor.testItem(power.get("item_condition"), e.getItem())) {
					boolean pass = power.getPowerFile().getFactoryProvider().getJsonArray("directions").isEmpty();
					for (BlockFace face : InstanceGetter.getBlockFaceFromDirection(power.getPowerFile().getFactoryProvider().getJsonArray("directions"))) {
						if (e.getBlockFace().equals(face)) {
							pass = true;
						}
					}
					if (!pass) return;
					setActive(e.getPlayer(), power.getTag(), true);
					Actions.BlockActionType(e.getClickedBlock().getLocation(), power.getBlockAction());
					Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
					Actions.ItemActionType(e.getItem(), power.getItemAction());
					Actions.ItemActionType(e.getItem(), power.getAction("held_item_action"));
					e.getPlayer().getInventory().addItem(power.getPowerFile().getFactoryProvider().getItemStack("result_stack"));
					Actions.ItemActionType(power.getPowerFile().getFactoryProvider().getItemStack("result_stack"), power.getAction("result_item_action"));
					tickFix.add(e.getPlayer());
					new BukkitRunnable() {
						@Override
						public void run() {
							setActive(e.getPlayer(), power.getTag(), false);
							tickFix.remove(e.getPlayer());
						}
					}.runTaskLater(GenesisMC.getPlugin(), 2L);
				}
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:action_on_block_use";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return action_on_block_use;
	}

	@Override
	public List<FactoryObjectInstance> getValidObjectFactory() {
		return super.getDefaultObjectFactory(List.of(
			new FactoryObjectInstance("entity_action", JSONObject.class, new JSONObject()),
			new FactoryObjectInstance("block_action", JSONObject.class, new JSONObject()),
			new FactoryObjectInstance("block_condition", JSONObject.class, new JSONObject()),
			new FactoryObjectInstance("item_condition", JSONObject.class, new JSONObject()),
			new FactoryObjectInstance("directions", JSONArray.class, new JSONArray()),
			new FactoryObjectInstance("result_stack", ItemStack.class, new ItemStack(Material.AIR)),
			new FactoryObjectInstance("result_item_action", JSONObject.class, new JSONObject()),
			new FactoryObjectInstance("held_item_action", JSONObject.class, new JSONObject())
		));
	}
}
