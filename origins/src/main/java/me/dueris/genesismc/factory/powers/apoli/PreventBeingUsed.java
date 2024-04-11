package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_being_used;

public class PreventBeingUsed extends CraftPower implements Listener {


    @EventHandler
    public void run(PlayerInteractEvent e) {
        if (prevent_being_used.contains(e.getPlayer())) {
            Player p = e.getPlayer();
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p) && ConditionExecutor.testItem(power.get("item_condition"), e.getItem())) {
                        setActive(p, power.getTag(), true);
                        e.setCancelled(true);
                    } else {

                        setActive(p, power.getTag(), false);
                    }
                }
            }
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:prevent_being_used";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return prevent_being_used;
    }
}
