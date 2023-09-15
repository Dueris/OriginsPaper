package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.actions.ActionTypes;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnBlockUse extends CraftPower implements Listener {
    Player p;

    public ActionOnBlockUse() {
        this.p = p;
    }

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void execute(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        Player actor = e.getPlayer();

        if (!getPowerArray().contains(actor)) return;

        for (OriginContainer origin : OriginPlayer.getOrigin(actor).values()) {
            for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                if (power == null) continue;
                ConditionExecutor conditionExecutor = new ConditionExecutor();
                if (conditionExecutor.check("condition", "conditions", actor, power, getPowerFile(), actor, null, e.getClickedBlock(), null, e.getItem(), null)) {
                    if (conditionExecutor.check("entity_condition", "entity_conditions", actor, power, getPowerFile(), actor, null, e.getClickedBlock(), null, e.getItem(), null)) {
                        if (conditionExecutor.check("block_condition", "block_conditions", actor, power, getPowerFile(), actor, null, e.getClickedBlock(), null, e.getItem(), null)) {
                            if (!getPowerArray().contains(e.getPlayer())) return;
                            setActive(power.getTag(), true);
                            ActionTypes.BlockActionType(e.getClickedBlock().getLocation(), power.getBlockAction());
                            ActionTypes.EntityActionType(e.getPlayer(), power.getEntityAction());
                            ActionTypes.ItemActionType(e.getPlayer().getActiveItem(), power.getItemAction());
                            ActionTypes.ItemActionType(e.getPlayer().getActiveItem(), power.getAction("held_item_action"));
                            ActionTypes.ItemActionType(e.getPlayer().getActiveItem(), power.getAction("result_item_action"));
                            ActionTypes.BlockEntityType(e.getPlayer(), e.getClickedBlock().getLocation(), power.getAction("block_entity_action"));
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (!getPowerArray().contains(e.getPlayer())) return;
                                    setActive(power.getTag(), false);
                                }
                            }.runTaskLater(GenesisMC.getPlugin(), 2L);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:action_on_block_use";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_block_use;
    }

    @Override
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }
}
