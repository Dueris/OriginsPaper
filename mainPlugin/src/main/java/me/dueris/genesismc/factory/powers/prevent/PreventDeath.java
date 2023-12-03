package me.dueris.genesismc.factory.powers.prevent;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.prevent.PreventSuperClass.prevent_death;

public class PreventDeath extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }


    @EventHandler
    public void run(PlayerDeathEvent e) {
        if (prevent_death.contains(e.getPlayer())) {
            for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (conditionExecutor.check("damage_condition", "damage_conditions", e.getPlayer(), power, "origins:prevent_death", e.getPlayer(), null, null, null, e.getPlayer().getItemInHand(), e.getPlayer().getLastDamageCause())) {
                        e.setCancelled(true);
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(power.getTag(), true);
                    } else {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(power.getTag(), false);
                    }
                }
            }
        }
    }

    Player p;

    public PreventDeath() {
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:prevent_death";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_death;
    }
}
