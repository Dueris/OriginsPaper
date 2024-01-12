package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOnDeath extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void d(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (getPowerArray().contains(p)) {
                for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                    ConditionExecutor executor = GenesisMC.getConditionExecutor();
                    for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (power == null) continue;
                        if (executor.check("damage_condition", "damage_conditions", p, power, getPowerFile(), p, null, p.getLocation().getBlock(), null, p.getInventory().getItemInMainHand(), null)) {
                            setActive(p, power.getTag(), true);
                            Actions.EntityActionType(p, power.getEntityAction());
                            if (power.getActionOrNull("bientity_action") != null) {
                                Actions.biEntityActionType(null, p, power.getBiEntityAction());
                            }
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    setActive(p, power.getTag(), false);
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
        return "origins:action_on_death";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_death;
    }

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
}
