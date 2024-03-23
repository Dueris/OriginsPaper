package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;

import java.util.ArrayList;

public class PreventItemPickup extends CraftPower implements Listener {

	@EventHandler
	public void pickup(PlayerAttemptPickupItemEvent e){
		Player p = e.getPlayer();
		if(this.getPowerArray().contains(p)){
			for(Layer layer : CraftApoli.getLayersFromRegistry()){
				for(Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)){
					boolean shouldCancel = ConditionExecutor.testItem(power.get("item_condition"), e.getItem().getItemStack()) && ConditionExecutor.testBiEntity(power.get("bientiy_condition"), (CraftEntity) p, (CraftEntity) e.getItem());
					if(shouldCancel) e.setCancelled(true);
					Actions.ItemActionType(e.getItem().getItemStack(), power.get("item_action"));
					Actions.BiEntityActionType(p, e.getItem(), power.get("bientiy_action_item"));
				}
			}
		}
	}

	@Override
	public void run(Player p) {

	}

	@Override
	public String getPowerFile() {
		return "apoli:prevent_item_pickup";
	}

	@Override
	public ArrayList<Player> getPowerArray() {
		return PreventSuperClass.prevent_item_pickup;
	}
}
