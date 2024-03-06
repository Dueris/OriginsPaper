package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnItemUse extends CraftPower implements Listener {

	public ActionOnItemUse() {

	}

	@Override
	public void run(Player p) {

	}

	@EventHandler
	public void entityRightClick(PlayerInteractEvent e) {
		Player player = e.getPlayer(); // aka "actor"
		if (!getPowerArray().contains(player)) return;
		if (!e.getAction().isRightClick()) return;

		for (Layer layer : CraftApoli.getLayersFromRegistry()) {
			for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
				if (power == null) continue;
				if (!ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) player)) return;
				if (!ConditionExecutor.testItem(power.get("condition"), e.getItem())) return;
				setActive(e.getPlayer(), power.getTag(), true);
				Actions.ItemActionType(e.getItem(), power.getAction("item_action"));
				Actions.EntityActionType(player, power.getAction("entity_action"));
				new BukkitRunnable() {
					@Override
					public void run() {
						setActive(e.getPlayer(), power.getTag(), false);
					}
				}.runTaskLater(GenesisMC.getPlugin(), 2L);
			}
		}
	}

	@Override
	public String getPowerFile() {
		return "apoli:action_on_item_use";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return action_on_item_use;
	}
}
