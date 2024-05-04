package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ActionOnItemPickup extends CraftPower implements Listener {

    @EventHandler
    public void pickup(PlayerAttemptPickupItemEvent e) {
        Player p = e.getPlayer();
        if (this.getPlayersWithPower().contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getPowers(p, getType(), layer)) {
                    if (!ConditionExecutor.testItem(power.getJsonObject("item_condition"), e.getItem().getItemStack()))
                        continue;
                    ItemStack clone = e.getItem().getItemStack().clone();
                    Actions.executeItem(clone, power.getJsonObject("item_action"));
                    // Needs to update the ItemEntity
                    e.getItem().setItemStack(clone);
                }
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:action_on_item_pickup";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return action_on_item_pickup;
    }
}
