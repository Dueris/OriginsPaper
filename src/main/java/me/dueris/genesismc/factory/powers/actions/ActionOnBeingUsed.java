package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOnBeingUsed extends CraftPower implements Listener {

    @EventHandler
    public void entityRightClickEntity(PlayerInteractEntityEvent e) {
        Entity actor = e.getPlayer();
        Entity target = e.getRightClicked();

        if (!(actor instanceof Player player)) return;
        if (!getPowerArray().contains(actor)) return;

        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            for (PowerContainer powerContainer : OriginPlayerUtils.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                PowerContainer power = powerContainer;
                if (power == null) continue;

                setActive(player, power.getTag(), true);
                Actions.biEntityActionType(actor, target, power.getBiEntityAction());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setActive(e.getPlayer(), power.getTag(), false);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 2L);
            }
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "origins:action_on_being_used";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_being_used;
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }
}
