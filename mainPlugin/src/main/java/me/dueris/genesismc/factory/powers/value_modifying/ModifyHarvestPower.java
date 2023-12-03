package me.dueris.genesismc.factory.powers.value_modifying;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.value_modifying.ValueModifyingSuperClass.modify_harvest;

public class ModifyHarvestPower extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }


    @EventHandler
    public void runD(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (modify_harvest.contains(p)) {
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("block_condition", "block_conditions", p, power, "origins:modify_harvest", p, null, e.getBlock(), null, p.getItemInHand(), null)) {
                        if (power.get("allow", null) == "true") {
                            e.setDropItems(false);

                            setActive(power.getTag(), true);
                        }
                    } else {

                        setActive(power.getTag(), false);
                    }
                }
            }
        }
    }

    Player p;

    public ModifyHarvestPower() {
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:modify_harvest";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_harvest;
    }
}
