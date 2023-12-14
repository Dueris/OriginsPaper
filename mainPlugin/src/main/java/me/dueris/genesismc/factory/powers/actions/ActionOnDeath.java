package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnDeath extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void d(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (getPowerArray().contains(p)) {
                Entity target = p;
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    ConditionExecutor executor = GenesisMC.getConditionExecutor();
                    for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                        if (power == null) continue;
                        if(executor.check("damage_condition", "damage_conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getInventory().getItemInMainHand(), null)){
                            setActive(power.getTag(), true);
                            Actions.EntityActionType(p, power.getEntityAction());
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    setActive(power.getTag(), false);
                                }
                            }.runTaskLater(GenesisMC.getPlugin(), 2L);
                        } 
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "genesis:action_on_death";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_death;
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }
}
