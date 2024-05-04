package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnEntityUse extends CraftPower implements Listener {

    private static final ArrayList<Player> cooldownTick = new ArrayList<>();

    @EventHandler
    public void entityRightClickEntity(PlayerInteractEntityEvent e) {
	Player actor = e.getPlayer();
	Entity target = e.getRightClicked();

	if (!getPlayersWithPower().contains(actor)) return;
	if (cooldownTick.contains(actor)) return;

	for (Layer layer : CraftApoli.getLayersFromRegistry()) {
	    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(actor, getType(), layer)) {
		if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) actor)) return;
		if (!ConditionExecutor.testItem(power.getJsonObject("item_condition"), actor.getInventory().getItem(e.getHand())))
		    return;
		if (!ConditionExecutor.testBiEntity(power.getJsonObject("bientity_condition"), (CraftEntity) actor, (CraftEntity) target))
		    return;
		cooldownTick.add(actor);
		setActive(e.getPlayer(), power.getTag(), true);
		Actions.executeBiEntity(actor, target, power.getJsonObject("bientity_action"));
		Actions.executeItem(actor.getActiveItem(), power.getJsonObject("held_item_action"));
		Actions.executeItem(actor.getActiveItem(), power.getJsonObject("result_item_action"));
		new BukkitRunnable() {
		    @Override
		    public void run() {
			setActive(e.getPlayer(), power.getTag(), false);
		    }
		}.runTaskLater(GenesisMC.getPlugin(), 2L);
		new BukkitRunnable() {
		    @Override
		    public void run() {
			cooldownTick.remove(actor);
		    }
		}.runTaskLater(GenesisMC.getPlugin(), 1);
	    }
	}
    }

    @Override
    public String getType() {
	return "apoli:action_on_entity_use";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
	return action_on_entity_use;
    }

}
