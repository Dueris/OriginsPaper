package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class StartingEquipmentPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }


    Player p;

    public StartingEquipmentPower() {
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void runGive(OriginChangeEvent e) {
        if (starting_equip.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if (conditionExecutor.check("condition", "conditions", e.getPlayer(), power, getPowerFile(), e.getPlayer(), null, null, null, e.getPlayer().getItemInHand(), null)) {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(power.getTag(), true);
                        runGiveItems(e.getPlayer(), power);
                    } else {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(power.getTag(), false);
                    }
                }
            }
        }
    }

    public void runGiveItems(Player p, PowerContainer power) {
        for (HashMap<String, Object> stack : power.getSingularAndPlural("stack", "stacks")) {
            p.getInventory().addItem(new ItemStack(Material.valueOf(stack.get("item").toString().toUpperCase().split(":")[1]), Integer.valueOf(power.get("amount", "1"))));
        }
    }

    @EventHandler
    public void runRespawn(PlayerRespawnEvent e) {
        if (starting_equip.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("condition", "conditions", e.getPlayer(), power, getPowerFile(), e.getPlayer(), null, null, null, e.getPlayer().getItemInHand(), null)) {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(power.getTag(), true);
                        if (power.get("recurrent", "false") != null) {
                            if (power.get("recurrent") == "true") {
                                runGiveItems(e.getPlayer(), power);
                            }
                        }
                    } else {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(power.getTag(), false);
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:starting_equipment";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return starting_equip;
    }
}
