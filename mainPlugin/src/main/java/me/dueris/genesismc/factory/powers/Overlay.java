package me.dueris.genesismc.factory.powers;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.player.Phasing;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class Overlay extends CraftPower {

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

    @Override
    public void run(Player player) {
        if (getPowerArray().contains(player)) {
            for (OriginContainer origin : OriginPlayerUtils.getOrigin(player).values()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("condition", "conditions", player, power, "origins:overlay", player, null, player.getLocation().getBlock(), null, player.getInventory().getItemInMainHand(), null)) {
                        setActive(player, power.getTag(), true);
                        Phasing.initializePhantomOverlay(player);
                    } else {
                        setActive(player, power.getTag(), false);
                        Phasing.deactivatePhantomOverlay(player);
                    }
                }
            }
        } else {
            Phasing.deactivatePhantomOverlay(player);
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:overlay";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return overlay;
    }
}
