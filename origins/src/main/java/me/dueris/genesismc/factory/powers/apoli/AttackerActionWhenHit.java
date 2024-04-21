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

public class AttackerActionWhenHit extends CraftPower implements Listener {

    @EventHandler
    public void a(EntityDamageByEntityEvent e) {
        Entity actor = e.getEntity();

        if (!(actor instanceof Player player)) return;
        if (!getPlayersWithPower().contains(actor)) return;

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getType(), layer)) {
                if (power == null) continue;
                if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) actor)) return;

                setActive(player, power.getTag(), true);
                Actions.executeBiEntity(actor, actor, power.getJsonObject("bientity_action"));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setActive(player, power.getTag(), false);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 2L);
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:attacker_action_when_hit";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return attacker_action_when_hit;
    }

}
