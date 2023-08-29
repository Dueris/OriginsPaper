package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
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
    public void setActive(String tag, Boolean bool) {
        if (powers_active.containsKey(tag)) {
            powers_active.replace(tag, bool);
        } else {
            powers_active.put(tag, bool);
        }
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (getPowerArray().contains(p)) {
                for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
                    ConditionExecutor conditionExecutor = new ConditionExecutor();
                    if (conditionExecutor.check("item_condition", "item_conditions", p, origin, getPowerFile(), null, p)) {
                        if (origin.getPowerFileFromType(getPowerFile()) == null) {
                            getPowerArray().remove(p);
                            return;
                        }
                        if (!getPowerArray().contains(p)) return;
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                        for (HashMap<String, Object> text : origin.getPowerFileFromType(getPowerFile()).getSingularAndPlural("text", "texts")) {
                            applyTooltip(p, p.getItemInHand(), text.get("text").toString());
                        }
                    } else {
                        if (origin.getPowerFileFromType(getPowerFile()) == null) {
                            getPowerArray().remove(p);
                            return;
                        }
                        if (!getPowerArray().contains(p)) return;
                        setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                        removeTooltip(p, p.getItemInHand());
                    }
                }
            } else {
                removeTooltip(p, p.getItemInHand());
            }
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
