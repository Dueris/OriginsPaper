package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class ActionOnBlockUse extends CraftPower implements Listener {

    public static ArrayList<Player> tickFix = new ArrayList<>();

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void execute(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getAction().isLeftClick() || e.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
        if (tickFix.contains(e.getPlayer())) return;
        Player actor = e.getPlayer();

        if (!getPowerArray().contains(actor)) return;

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(actor, getPowerFile(), layer)) {
                if (power == null) continue;
                if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) e.getPlayer()) &&
                        ConditionExecutor.testBlock((JSONObject) power.get("block_condition"), (CraftBlock) e.getClickedBlock()) &&
                        ConditionExecutor.testItem((JSONObject) power.get("item_condition"), e.getItem())) {
                    setActive(e.getPlayer(), power.getTag(), true);
                    Actions.BlockActionType(e.getClickedBlock().getLocation(), power.getBlockAction());
                    Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
                    Actions.ItemActionType(e.getItem(), power.getItemAction());
                    Actions.ItemActionType(e.getItem(), power.getAction("held_item_action"));
                    if (power.getOrDefault("result_stack", null) != null) {
                        JSONObject jsonObject = power.get("result_stack");
                        int amt;
                        if (jsonObject.get("amount").toString() != null) {
                            amt = Integer.parseInt(jsonObject.get("amount").toString());
                        } else {
                            amt = 1;
                        }
                        ItemStack itemStack = new ItemStack(Material.valueOf(jsonObject.get("item").toString().toUpperCase().split(":")[jsonObject.get("item").toString().split(":").length]), amt);
                        e.getPlayer().getInventory().addItem(itemStack);
                        Actions.ItemActionType(itemStack, power.getAction("result_item_action"));
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
    public String getPowerFile() {
        return "apoli:action_on_block_use";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_block_use;
    }

}
