package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ItemOnItemPower extends CraftPower implements Listener {

    @EventHandler
    public void itemOnItem(InventoryClickEvent e) {
        if (e.getCursor() != null && e.getCurrentItem() != null) { // Valid event
            Player p = (Player) e.getWhoClicked();
            if (p.getGameMode().equals(GameMode.CREATIVE)) return;
            if (this.getPlayersWithPower().contains(p)) {
                for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getType(), layer)) {
                        boolean pass =
                            ConditionExecutor.testItem(power.getJsonObject("using_item_condition"), e.getCursor()) &&
                                ConditionExecutor.testItem(power.getJsonObject("on_item_condition"), e.getCurrentItem());
                        if (pass) {
                            ItemStack stack = power.getItemStack("result");
                            if (stack != null) {
                                Actions.executeItem(stack, power.getJsonObject("result_item_action"));
                                for (int i = 0; i < power.getNumberOrDefault("result_from_on_stack", 1).getInt(); i++) {
                                    p.getInventory().addItem(stack);
                                }
                            }
                            Actions.executeItem(e.getCursor(), power.getJsonObject("using_item_action"));
                            Actions.executeItem(e.getCurrentItem(), power.getJsonObject("on_item_action"));
                            Actions.executeEntity(e.getWhoClicked(), power.getJsonObject("entity_action"));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getType() {
        return "apoli:item_on_item";
    }

    @Override
    public ArrayList<Player> getPlayersWithPower() {
        return item_on_item;
    }
}
