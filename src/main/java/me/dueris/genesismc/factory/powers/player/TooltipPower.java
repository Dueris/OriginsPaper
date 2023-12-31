package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.ChatColor;
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
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    @Override
    public void run(Player p) {
        if (getPowerArray().contains(p)) {
            for (me.dueris.genesismc.utils.LayerContainer layer : me.dueris.genesismc.factory.CraftApoli.getLayers()) {
                ConditionExecutor conditionExecutor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                for (PowerContainer power : OriginPlayerUtils.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    if (conditionExecutor.check("item_condition", "item_conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {

                        setActive(p, power.getTag(), true);
                        for (HashMap<String, Object> text : power.getSingularAndPlural("text", "texts")) {
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
        return "origins:tooltip";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return tooltip;
    }
}
