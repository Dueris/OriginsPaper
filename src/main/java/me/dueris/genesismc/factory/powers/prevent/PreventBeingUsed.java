package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_being_used;

public class PreventBeingUsed extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }


    @EventHandler
    public void run(PlayerInteractEvent e) {
        if (prevent_being_used.contains(e.getPlayer())) {
            Player p = e.getPlayer();
            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("bientity_condition", "bientity_conditions", p, power, "origins:prevent_being_used", p, null, null, null, p.getItemInHand(), null)) {
                        if (conditionExecutor.check("item_condition", "item_conditions", p, power, "origins:prevent_being_used", p, null, null, null, p.getItemInHand(), null)) {

                            setActive(power.getTag(), true);
                            e.setCancelled(true);
                        } else {

                            setActive(power.getTag(), false);
                        }
                    } else {

                        setActive(power.getTag(), false);
                    }
                }
            }
        }
    }

    Player p;

    public PreventBeingUsed() {
        this.p = p;
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
