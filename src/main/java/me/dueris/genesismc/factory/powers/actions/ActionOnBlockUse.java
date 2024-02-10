package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.console.OriginConsoleSender;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOnBlockUse extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    public static ArrayList<Player> tickFix = new ArrayList<>();

    @EventHandler
    public void execute(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getAction().isLeftClick() || e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
        if (tickFix.contains(e.getPlayer())) return;
        Player actor = e.getPlayer();

        if (!getPowerArray().contains(actor)) return;

        for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
            for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(actor, getPowerFile(), layer)) {
                if (power == null) continue;
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                if (conditionExecutor.check("condition", "conditions", actor, power, getPowerFile(), actor, null, e.getClickedBlock(), null, e.getItem(), null)) {
                    if (conditionExecutor.check("entity_condition", "entity_conditions", actor, power, getPowerFile(), actor, null, e.getClickedBlock(), null, e.getItem(), null)) {
                        if (conditionExecutor.check("block_condition", "block_conditions", actor, power, getPowerFile(), actor, null, e.getClickedBlock(), null, e.getItem(), null)) {
                            if (conditionExecutor.check("item_condition", "item_conditions", actor, power, getPowerFile(), actor, null, e.getClickedBlock(), null, e.getItem(), null)){
                                setActive(e.getPlayer(), power.getTag(), true);
                                Actions.BlockActionType(e.getClickedBlock().getLocation(), power.getBlockAction());
                                Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
                                Actions.ItemActionType(e.getItem(), power.getItemAction());
                                Actions.ItemActionType(e.getItem(), power.getAction("held_item_action"));
                                Actions.ItemActionType(e.getItem(), power.getAction("result_item_action"));
                                if(power.getOrDefault("result_stack", null) != null){
                                    e.getItem().setAmount(e.getItem().getAmount() - 1);
                                    final boolean lastSendCMDFeedback = Boolean.parseBoolean(GameRule.SEND_COMMAND_FEEDBACK.toString());
                                    final boolean lastlogAdminCMDs = Boolean.parseBoolean(GameRule.LOG_ADMIN_COMMANDS.toString());
                                    e.getPlayer().getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
                                    e.getPlayer().getWorld().setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
                                    Bukkit.dispatchCommand(new OriginConsoleSender(), "give {p} {t}{n} 1".replace("{p}", e.getPlayer().getName())
                                            .replace("{t}", power.get("result_stack").get("item").toString())
                                            .replace("{n}", power.get("result_stack").getOrDefault("tag", "{}").toString())
                                            .replace("{c}", power.get("result_stack").getOrDefault("amount", "1").toString())
                                    );
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            e.getPlayer().getWorld().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, lastSendCMDFeedback);
                                            e.getPlayer().getWorld().setGameRule(GameRule.LOG_ADMIN_COMMANDS, lastlogAdminCMDs);
                                        }
                                    }.runTaskLater(GenesisMC.getPlugin(), 1);
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
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:action_on_block_use";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_block_use;
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
