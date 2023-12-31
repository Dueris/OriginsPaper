package me.dueris.genesismc.factory.powers.player.inventory;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class KeepInventory extends CraftPower implements Listener {

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }


    @EventHandler
    public void keepinv(PlayerDeathEvent e) {
        Player player = e.getEntity();
        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            if (keep_inventory.contains(player)) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                    if (conditionExecutor.check("item_condition", "item_conditions", player, power, "origins:keep_inventory", player, null, null, null, player.getInventory().getItemInHand(), null)) {
                        ArrayList<Long> slots = new ArrayList<>();
                        setActive(player, power.getTag(), true);
                        if (power.getSlots() != null) {
                            for (long slot : power.getSlots()) {
                                slots.add(slot);
                            }
                        }

                        if (!slots.isEmpty()) {
                            for (int i = 0; i < player.getInventory().getSize(); i++) {
                                if (slots.contains((long) i)) {
                                    e.getItemsToKeep().add(player.getInventory().getItem(i));
                                }
                            }
                        }
                    } else {
                        setActive(player, power.getTag(), false);
                    }
                }
            }
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:keep_inventory";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return keep_inventory;
    }
}
