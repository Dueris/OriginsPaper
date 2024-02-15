package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.LayerContainer;
import me.dueris.genesismc.registry.PowerContainer;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
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

        for (LayerContainer layer : CraftApoli.getLayers()) {
            ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
            if (CooldownUtils.isPlayerInCooldown(player, getPowerFile())) return;
            for (PowerContainer power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                if (executor.check("condition", "conditions", (Player) target, power, getPowerFile(), target, null, null, null, player.getInventory().getItemInHand(), null)) {
                    setActive(player, power.getTag(), true);
                    Actions.EntityActionType(target, power.getEntityAction());
                    if (power.getObjectOrDefault("cooldown", 1) != null) {
                        CooldownUtils.addCooldown((Player) target, Utils.getNameOrTag(power), power.getType(), power.getIntOrDefault("cooldown", power.getIntOrDefault("max", 1)), getPowerFile());
                    }
                } else {
                    setActive(player, power.getTag(), false);
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:self_action_on_kill";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return self_action_on_kill;
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
