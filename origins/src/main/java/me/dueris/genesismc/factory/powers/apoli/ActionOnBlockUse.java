package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class ActionOnBlockUse extends CraftPower implements Listener {

    public static ArrayList<Player> tickFix = new ArrayList<>();

    @EventHandler
    public void execute(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getAction().isLeftClick() || e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
        if (tickFix.contains(e.getPlayer())) return;
        Player actor = e.getPlayer();

        if (!getPlayersWithPower().contains(actor)) return;

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(actor, getType(), layer)) {
                if (power == null) continue;
                if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) e.getPlayer()) &&
                    ConditionExecutor.testBlock(power.getJsonObject("block_condition"), (CraftBlock) e.getClickedBlock()) &&
                    ConditionExecutor.testItem(power.getJsonObject("item_condition"), e.getItem())) {
                    setActive(e.getPlayer(), power.getTag(), true);
                    Actions.executeBlock(e.getClickedBlock().getLocation(), power.getJsonObject("block_action"));
                    Actions.executeEntity(e.getPlayer(), power.getJsonObject("entity_action"));
                    Actions.executeItem(e.getItem(), power.getJsonObject("item_action"));
                    Actions.executeItem(e.getItem(), power.getJsonObject("held_item_action"));
                    if (power.isPresent("result_stack")) {
                        EdibleItem.runResultStack(power, true, e.getPlayer());
                    }
                    tickFix.add(e.getPlayer());
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            setActive(e.getPlayer(), power.getTag(), false);
                            tickFix.remove(e.getPlayer());
                        }
                    }.runTaskLater(GenesisMC.getPlugin(), 2L);
                }
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:action_on_block_use";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return action_on_block_use;
    }

}
