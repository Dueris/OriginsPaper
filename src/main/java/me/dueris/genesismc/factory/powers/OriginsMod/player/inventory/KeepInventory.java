package me.dueris.genesismc.factory.powers.OriginsMod.player.inventory;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;

public class KeepInventory extends CraftPower implements Listener {

    @Override
    public void setActive(Boolean bool){
        if(powers_active.containsKey(getPowerFile())){
            powers_active.replace(getPowerFile(), bool);
        }else{
            powers_active.put(getPowerFile(), bool);
        }
    }

    @Override
    public Boolean getActive(){
        return powers_active.get(getPowerFile());
    }

    @EventHandler
    public void keepinv(PlayerDeathEvent e) {
        Player player = e.getEntity();
        for (OriginContainer origin : OriginPlayer.getOrigin(player).values()) {
            if (keep_inventory.contains(player)) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if (conditionExecutor.check("item_condition", "item_conditions", player, origin, "origins:keep_inventory", null, player)) {
                    ArrayList<Long> slots = new ArrayList<>();
                    setActive(true);
                    if (origin.getPowerFileFromType("origins:keep_inventory").getSlots() != null) {
                        for (long slot : origin.getPowerFileFromType("origins:keep_inventory").getSlots()) {
                            slots.add(slot);
                        }
                    }

                    if (!slots.isEmpty()) {
                        for (int i = 0; i < player.getInventory().getSize(); i++) {
                            if (slots.contains((long) i)) {
                                e.getItemsToKeep().add(player.getInventory().getItem(i));
                            }
                        }
                    } else {
                        e.setKeepInventory(true);
                    }
                }else{
                    setActive(false);
                }
            }
        }
    }

    @Override
    public void run() {

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
