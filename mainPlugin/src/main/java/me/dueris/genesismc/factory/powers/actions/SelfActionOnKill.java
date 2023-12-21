package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.CooldownManager;
import me.dueris.genesismc.entity.OriginPlayerUtils;
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

import java.util.ArrayList;
import java.util.HashMap;

public class SelfActionOnKill extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void k(EntityDeathEvent e) {
        Entity target = e.getEntity();

        if (!(target instanceof Player player)) return;
        if (!getPowerArray().contains(target)) return;

        for (OriginContainer origin : OriginPlayerUtils.getOrigin(player).values()) {
            ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
            if (CooldownManager.isPlayerInCooldown((Player) target, "key.attack")) return;
            for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                if (executor.check("condition", "conditions", (Player) target, power, getPowerFile(), target, null, null, null, player.getInventory().getItemInHand(), null)) {
                    setActive(player, power.getTag(), true);
                    Actions.EntityActionType(target, power.getEntityAction());
                    if (power.get("cooldown", "1") != null) {
                        CooldownManager.addCooldown((Player) target, origin, power.getTag(), power.getType(), Integer.parseInt(power.get("cooldown", "1")), "key.attack");
                    }
                } else {
                    setActive(player, power.getTag(), false);
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:self_action_on_kill";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return self_action_on_kill;
    }

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
}
