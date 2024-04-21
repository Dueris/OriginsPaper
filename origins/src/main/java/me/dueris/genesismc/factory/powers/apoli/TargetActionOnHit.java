package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class TargetActionOnHit extends CraftPower implements Listener {

    @EventHandler
    public void s(EntityDamageByEntityEvent e) {
        Entity actor = e.getDamager();
        Entity target = e.getEntity();

        if (!(actor instanceof Player player)) return;
        if (!getPlayersWithPower().contains(actor)) return;

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getType(), layer)) {
                if (CooldownUtils.isPlayerInCooldownFromTag(player, Utils.getNameOrTag(power))) continue;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) player)) {
                            setActive(player, power.getTag(), true);
                            Actions.executeEntity(target, power.getJsonObject("entity_action"));
                            if (power.isPresent("cooldown")) {
                                CooldownUtils.addCooldown((Player) actor, Utils.getNameOrTag(power), power.getNumberOrDefault("cooldown", power.getNumberOrDefault("max", 1).getInt()).getInt(), power.getJsonObject("hud_render"));
                            }
                        } else {
                            setActive(player, power.getTag(), false);
                        }
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 1);
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:target_action_on_hit";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return target_action_on_hit;
    }

}
