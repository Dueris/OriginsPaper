package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_harvest;

public class ModifyHarvestPower extends CraftPower implements Listener {

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


    @EventHandler
    public void runD(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (modify_harvest.contains(p)) {
            if (p.getGameMode().equals(GameMode.CREATIVE)) return;
            try {
                for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                    ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (conditionExecutor.check("block_condition", "block_conditions", p, power, "apoli:modify_harvest", p, null, e.getBlock(), null, p.getItemInHand(), null)) {
                            e.setDropItems(false);
                            setActive(p, power.getTag(), true);
                            if (power.getBooleanOrDefault("allow", true) && !e.isDropItems()) {
                                e.getBlock().getDrops().forEach((itemStack -> p.getWorld().dropItemNaturally(e.getBlock().getLocation(), itemStack)));
                            }
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                }
            } catch (Exception ee) {

            }
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_harvest";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_harvest;
    }
}
