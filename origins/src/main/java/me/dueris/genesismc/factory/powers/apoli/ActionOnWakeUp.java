package me.dueris.genesismc.factory.powers.apoli;

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
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.ArrayList;

public class ActionOnWakeUp extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void w(PlayerBedLeaveEvent e) {
        if (!getPowerArray().contains(e.getPlayer())) return;
        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(e.getPlayer(), getPowerFile(), layer)) {
                if (!ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) e.getPlayer())) return;
                setActive(e.getPlayer(), power.getTag(), true);
                Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
                Actions.BlockActionType(e.getBed().getLocation(), power.getBlockAction());
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:action_on_wake_up";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_wake_up;
    }

}
