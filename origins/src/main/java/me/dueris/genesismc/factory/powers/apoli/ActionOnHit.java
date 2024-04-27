package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnHit extends CraftPower implements Listener {

    @EventHandler
    public void action(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p) {
            Entity target = e.getEntity();
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                if (getPlayersWithPower().contains(p)) {
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                        if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p))
                            return;
                        if (!ConditionExecutor.testDamage(power.getJsonObject("damage_condition"), e)) return;
                        if (!ConditionExecutor.testBiEntity(power.getJsonObject("bientity_condition"), (CraftEntity) p, (CraftEntity) target))
                            return;
                        setActive(p, power.getTag(), true);
                        Actions.executeEntity(p, power.getJsonObject("entity_action"));
                        Actions.executeBiEntity(p, target, power.getJsonObject("bientity_action"));
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

    @Override
    public String getType() {
        return "apoli:action_on_hit";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return action_on_hit;
    }

}
