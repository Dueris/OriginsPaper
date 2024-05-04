package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

import static me.dueris.genesismc.factory.powers.apoli.superclass.PreventSuperClass.prevent_item_use;

public class PreventItemUse extends CraftPower implements Listener {

    @EventHandler
    public void runD(PlayerInteractEvent e) {
        if (prevent_item_use.contains(e.getPlayer())) {
            if (e.getItem() == null) return;

            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getPowers(e.getPlayer(), getType(), layer)) {
                    if (power == null) {
                        getPlayersWithPower().remove(e.getPlayer());
                        return;
                    } else {
                        boolean shouldCancel = ConditionExecutor.testItem(power.getJsonObject("item_condition"), e.getItem());
                        if (shouldCancel) e.setCancelled(true);
                    }
                }
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:prevent_item_use";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return prevent_item_use;
    }
}
