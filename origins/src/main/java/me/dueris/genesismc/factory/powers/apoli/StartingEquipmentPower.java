package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class StartingEquipmentPower extends CraftPower implements Listener {


    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void runGive(OriginChangeEvent e) {
        if (starting_equip.contains(e.getPlayer())) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power, power.get("condition"), (CraftEntity) e.getPlayer())) {
                        setActive(e.getPlayer(), power.getTag(), true);
                        runGiveItems(e.getPlayer(), power);
                    } else {
                        setActive(e.getPlayer(), power.getTag(), false);
                    }
                }
            }
        }
    }

    public void runGiveItems(Player p, Power power) {
        for (HashMap<String, Object> stack : power.getJsonListSingularPlural("stack", "stacks")) {
            p.getInventory().addItem(new ItemStack(Material.valueOf(stack.get("item").toString().toUpperCase().split(":")[1]), power.getIntOrDefault("amount", 1)));
        }
    }

    @EventHandler
    public void runRespawn(PlayerRespawnEvent e) {
        if (starting_equip.contains(e.getPlayer())) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power, power.get("condition"), (CraftEntity) e.getPlayer())) {
                        setActive(e.getPlayer(), power.getTag(), true);
                        if (power.getObject("recurrent") != null && power.getBoolean("recurrent")) {
                            runGiveItems(e.getPlayer(), power);
                        }
                    } else {
                        setActive(e.getPlayer(), power.getTag(), false);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:starting_equipment";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return starting_equip;
    }
}
