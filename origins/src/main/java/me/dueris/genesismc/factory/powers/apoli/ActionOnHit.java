package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnHit extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void action(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player p) {
            Player actor = p;
            Entity target = e.getEntity();
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                if (getPowerArray().contains(p)) {
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (!ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) actor)) return;
                        if (!ConditionExecutor.testDamage(power.get("damage_condition"), e)) return;
                        if (!ConditionExecutor.testBiEntity(power.get("bientity_condition"), (CraftEntity) actor, (CraftEntity) target))
                            return;
                        setActive(p, power.getTag(), true);
                        Actions.executeEntity(actor, power.getEntityAction());
                        Actions.executeBiEntity(actor, target, power.getBiEntityAction());
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
    public String getPowerFile() {
        return "apoli:action_on_hit";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_hit;
    }

}
