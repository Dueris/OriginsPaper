package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.events.OriginChangeEvent;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
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
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void runGive(OriginChangeEvent e) {
        if (starting_equip.contains(e.getPlayer())) {
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    if (conditionExecutor.check("condition", "conditions", e.getPlayer(), power, getPowerFile(), e.getPlayer(), null, null, null, e.getPlayer().getItemInHand(), null)) {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(e.getPlayer(), power.getTag(), true);
                        runGiveItems(e.getPlayer(), power);
                    } else {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(e.getPlayer(), power.getTag(), false);
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
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                    if (conditionExecutor.check("condition", "conditions", e.getPlayer(), power, getPowerFile(), e.getPlayer(), null, null, null, e.getPlayer().getItemInHand(), null)) {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(e.getPlayer(), power.getTag(), true);
                        if (power.get("recurrent", "false") != null) {
                            if (power.get("recurrent") == "true") {
                                runGiveItems(e.getPlayer(), power);
                            }
                        }
                    } else {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(e.getPlayer(), power.getTag(), false);
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
