package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;

public class SelfActionOnKill extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void k(EntityDeathEvent e) {
        Entity target = e.getEntity();

        if (!(target instanceof Player player)) return;
        if (!getPowerArray().contains(target)) return;

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                if (CooldownUtils.isPlayerInCooldownFromTag(player, Utils.getNameOrTag(power))) continue;
                if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) player)) {
                    setActive(player, power.getTag(), true);
                    Actions.EntityActionType(target, power.getEntityAction());
                    if (power.getObjectOrDefault("cooldown", 1) != null) {
                        CooldownUtils.addCooldown((Player) target, Utils.getNameOrTag(power), power.getType(), power.getIntOrDefault("cooldown", power.getIntOrDefault("max", 1)), power.get("hud_render"));
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

}
