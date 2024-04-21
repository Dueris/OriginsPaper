package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.GameEvent;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnLand extends CraftPower implements Listener {

    @EventHandler
    public void e(GenericGameEvent e) {
        if (e.getEvent() != GameEvent.HIT_GROUND) return;
        if (!(e.getEntity() instanceof Player player)) return;
        if (!getPowerArray().contains(player)) return;
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                if (!ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) player)) return;
                setActive(player, power.getTag(), true);
                Actions.executeEntity(player, power.getJsonObject("entity_action"));
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
    public String getPowerFile() {
        return "apoli:action_on_land";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_land;
    }

}
