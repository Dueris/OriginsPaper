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
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnCallback extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void choose(OriginChangeEvent e) {
        Player actor = e.getPlayer();

        if (!getPowerArray().contains(actor)) return;

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(actor, getPowerFile(), layer)) {
                if (power == null) continue;
                if (!ConditionExecutor.testEntity(power, power.get("condition"), (CraftEntity) e.getPlayer())) return;
                if (power.getBooleanOrDefault("execute_chosen_when_orb", false) && !e.isFromOrb()) return;
                setActive(e.getPlayer(), power.getTag(), true);
                Actions.executeEntity(power, e.getPlayer(), power.getEntityAction());
                Actions.executeEntity(power, e.getPlayer(), power.get("entity_action_chosen"));
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
        if (!getPowerArray().contains(player)) return;
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                if (e.isRemoved()) {
                    Actions.executeEntity(power, e.getPlayer(), power.get("entity_action_removed"));
                } else {
                    Actions.executeEntity(power, e.getPlayer(), power.get("entity_action_added"));
                }
            }
        }
    }

    @EventHandler
    public void respawn(PlayerPostRespawnEvent e) {
        Player player = e.getPlayer();
        if (!getPowerArray().contains(player)) return;
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                Actions.executeEntity(power, e.getPlayer(), power.get("entity_action_respawned"));
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:action_on_callback";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_callback;
    }

}
