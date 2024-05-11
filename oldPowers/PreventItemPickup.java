package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

import java.util.ArrayList;

public class PreventItemPickup extends CraftPower implements Listener {

	@EventHandler
	public void pickup(PlayerAttemptPickupItemEvent e) {
		Player p = e.getPlayer();
		if (this.getPlayersWithPower().contains(p)) {
			for (Power power : OriginPlayerAccessor.getPowers(p, getType())) {
				boolean shouldCancel = ConditionExecutor.testItem(power.getJsonObject("item_condition"), e.getItem().getItemStack()) && ConditionExecutor.testBiEntity(power.getJsonObject("bientiy_condition"), (CraftEntity) p, (CraftEntity) e.getItem());
				if (shouldCancel) e.setCancelled(true);
				Actions.executeItem(e.getItem().getItemStack(), power.getJsonObject("item_action"));
				Actions.executeBiEntity(p, e.getItem(), power.getJsonObject("bientiy_action_item"));
			}
		}
	}

	@Override
	public String getType() {
		return "apoli:prevent_item_pickup";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return PreventSuperClass.prevent_item_pickup;
	}
}
