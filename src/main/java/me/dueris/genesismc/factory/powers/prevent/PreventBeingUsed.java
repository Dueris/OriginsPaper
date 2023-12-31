package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_being_used;

public class PreventBeingUsed extends CraftPower implements Listener {

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
    public void run(PlayerInteractEvent e) {
        if (prevent_being_used.contains(e.getPlayer())) {
            Player p = e.getPlayer();
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (conditionExecutor.check("bientity_condition", "bientity_conditions", p, power, "origins:prevent_being_used", p, null, null, null, p.getItemInHand(), null)) {
                        if (conditionExecutor.check("item_condition", "item_conditions", p, power, "origins:prevent_being_used", p, null, null, null, p.getItemInHand(), null)) {

                            setActive(p, power.getTag(), true);
                            e.setCancelled(true);
                        } else {

                            setActive(p, power.getTag(), false);
                        }
                    } else {

                        setActive(p, power.getTag(), false);
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
        return "origins:prevent_being_used";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_being_used;
    }
}
