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
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnDeath extends CraftPower implements Listener {

    @EventHandler
    public void d(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (getPowerArray().contains(p)) {
                for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (power == null) continue;
                        if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p) && !ConditionExecutor.testDamage(power.getJsonObject("damage_condition"), e.getEntity().getLastDamageCause()))
                            return;
                        setActive(p, power.getTag(), true);
                        Actions.executeEntity(p, power.getJsonObject("entity_action"));
                        if (((CraftPlayer) p).getHandle().getLastHurtByMob() != null) {
                            Actions.executeBiEntity(((CraftPlayer) p).getHandle().getLastHurtByMob().getBukkitEntity(), p/* player is target? */, power.getJsonObject("bientity_action"));
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

    @Override
    public String getPowerFile() {
        return "apoli:action_on_death";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_death;
    }

}
