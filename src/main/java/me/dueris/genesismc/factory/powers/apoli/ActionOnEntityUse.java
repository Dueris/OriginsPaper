package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOnEntityUse extends CraftPower implements Listener {

    private static final ArrayList<Player> cooldownTick = new ArrayList<>();

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void entityRightClickEntity(PlayerInteractEntityEvent e) {
        Player actor = e.getPlayer();
        Entity target = e.getRightClicked();

        if (!getPowerArray().contains(actor)) return;
        if (cooldownTick.contains(actor)) return;

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(actor, getPowerFile(), layer)) {
                if (GenesisMC.getConditionExecutor().check("condition", "conditions", actor, power, getPowerFile(), actor, target, actor.getLocation().getBlock(), null, actor.getActiveItem(), null)) {
                    if (GenesisMC.getConditionExecutor().check("item_condition", "item_conditions", actor, power, getPowerFile(), actor, target, actor.getLocation().getBlock(), null, actor.getActiveItem(), null)) {
                        if (GenesisMC.getConditionExecutor().check("bientity_condition", "bientity_conditions", actor, power, getPowerFile(), actor, target, actor.getLocation().getBlock(), null, actor.getActiveItem(), null)) {
                            if (power == null) continue;
                            cooldownTick.add(actor);
                            setActive(e.getPlayer(), power.getTag(), true);
                            Actions.BiEntityActionType(actor, target, power.getBiEntityAction());
                            Actions.ItemActionType(actor.getActiveItem(), power.getAction("held_item_action"));
                            Actions.ItemActionType(actor.getActiveItem(), power.getAction("result_item_action"));
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    setActive(e.getPlayer(), power.getTag(), false);
                                }
                            }.runTaskLater(GenesisMC.getPlugin(), 2L);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    cooldownTick.remove(actor);
                                }
                            }.runTaskLater(GenesisMC.getPlugin(), 1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:action_on_entity_use";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_entity_use;
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
