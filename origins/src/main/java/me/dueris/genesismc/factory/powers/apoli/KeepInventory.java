package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;

public class KeepInventory extends CraftPower implements Listener {


    @EventHandler
    public void keepinv(PlayerDeathEvent e) {
        Player player = e.getEntity();
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            if (keep_inventory.contains(player)) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power, power.get("condition"), (CraftEntity) player)) {
                        ArrayList<Long> slots = new ArrayList<>();
                        setActive(player, power.getTag(), true);
                        if (power.getLongList("slots") != null) {
                            slots.addAll(power.getLongList("slots"));
                        }

                        if (!slots.isEmpty()) {
                            for (int i = 0; i < player.getInventory().getSize(); i++) {
                                if (slots.contains((long) i)) {
                                    if (ConditionExecutor.testItem(power.get("item_condition"), player.getInventory().getItem(i))) {
                                        e.getItemsToKeep().add(player.getInventory().getItem(i));
                                    }
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
        return "apoli:keep_inventory";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return keep_inventory;
    }
}
