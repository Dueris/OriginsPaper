package me.dueris.genesismc.factory.powers.apoli;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.OriginChangeEvent;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnCallback extends CraftPower implements Listener {

    @EventHandler
    public void choose(OriginChangeEvent e) {
        Player actor = e.getPlayer();

        if (!getPlayersWithPower().contains(actor)) return;

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(actor, getType(), layer)) {
                if (power == null) continue;
                if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) e.getPlayer()))
                    return;
                if (power.getBooleanOrDefault("execute_chosen_when_orb", false) && !e.isFromOrb()) return;
                setActive(e.getPlayer(), power.getTag(), true);
                Actions.executeEntity(e.getPlayer(), power.getJsonObject("entity_action"));
                Actions.executeEntity(e.getPlayer(), power.getJsonObject("entity_action_chosen"));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setActive(e.getPlayer(), power.getTag(), false);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 2L);
            }
        }
    }

    @EventHandler
    public void powerUpdate(PowerUpdateEvent e) {
        Player player = e.getPlayer();
        if (!getPlayersWithPower().contains(player)) return;
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getType(), layer)) {
                if (e.isRemoved()) {
                    Actions.executeEntity(e.getPlayer(), power.getJsonObject("entity_action_removed"));
                } else {
                    Actions.executeEntity(e.getPlayer(), power.getJsonObject("entity_action_added"));
                }
            }
        }
    }

    @EventHandler
    public void respawn(PlayerPostRespawnEvent e) {
        Player player = e.getPlayer();
        if (!getPlayersWithPower().contains(player)) return;
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getType(), layer)) {
                Actions.executeEntity(e.getPlayer(), power.getJsonObject("entity_action_respawned"));
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:action_on_callback";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return action_on_callback;
    }

}
