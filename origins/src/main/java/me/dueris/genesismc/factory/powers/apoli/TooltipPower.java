package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class TooltipPower extends CraftPower {

    public static void applyTooltip(Player player, ItemStack itemStack, String tooltip) {
        if (player == null || itemStack == null) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        String tooltipToApply = ChatColor.DARK_GRAY + tooltip;
        itemMeta.setLore(itemMeta.getLore());
        itemMeta.getLore().add(tooltipToApply);

        itemStack.setItemMeta(itemMeta);
    }

    public static void removeTooltip(Player player, ItemStack itemStack) {
        if (player == null || itemStack == null) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        if (itemMeta.getLore() == null) return;
        for (String lore : itemMeta.getLore()) {
            itemMeta.getLore().remove(lore);
        }
        itemStack.setItemMeta(itemMeta);
    }


    @Override
    public void run(Player p) {
        if (getPowerArray().contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (ConditionExecutor.testEntity(power.getJsonObjectOrNew("condition"), (CraftEntity) p) && ConditionExecutor.testItem(power.getJsonObjectOrNew("item_condition"), p.getItemInHand())) {
                        setActive(p, power.getTag(), true);
                        for (HashMap<String, Object> text : power.getList$SingularPlural("text", "texts")) {
                            applyTooltip(p, p.getItemInHand(), text.get("text").toString());
                        }
                    } else {

                        setActive(p, power.getTag(), false);
                        removeTooltip(p, p.getItemInHand());
                    }
                }
            }
        } else {
            removeTooltip(p, p.getItemInHand());
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:tooltip";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return tooltip;
    }
}
