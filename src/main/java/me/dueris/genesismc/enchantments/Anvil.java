package me.dueris.genesismc.enchantments;

import me.dueris.genesismc.Bootstrap;
import me.dueris.genesismc.GenesisMC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.chat.BaseComponent;

import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static me.dueris.genesismc.GenesisMC.waterProtectionEnchant;

public class Anvil implements Listener {
    private static EquipmentSlot[] slots = {EquipmentSlot.CHEST, EquipmentSlot.FEET, EquipmentSlot.HEAD, EquipmentSlot.LEGS};
    public static @Nullable net.minecraft.world.item.enchantment.Enchantment eimpl = CraftEnchantment.bukkitToMinecraft(CraftRegistry.ENCHANTMENT.get(new NamespacedKey("origins", "water_protection")));
    public static ArrayList<Enchantment> conflictenchantments = new ArrayList<>();
    static {
        conflictenchantments.add(Enchantment.PROTECTION_FIRE);
        conflictenchantments.add(Enchantment.PROTECTION_ENVIRONMENTAL);
        conflictenchantments.add(Enchantment.PROTECTION_EXPLOSIONS);
        conflictenchantments.add(Enchantment.PROTECTION_FALL);
        conflictenchantments.add(Enchantment.PROTECTION_PROJECTILE);
    }

    private boolean isCompatibleWith(net.minecraft.world.item.enchantment.Enchantment other){
        for(org.bukkit.enchantments.Enchantment enchant : Anvil.conflictenchantments){
            CraftEnchantment enchantt = (CraftEnchantment) enchant;
            if(other == enchantt.getHandle()){
                return true;
            }
        }
        return false;
    }

    public static void setWaterProtCustomEnchantLevel(int lvl, ItemStack item) {
        String level = numberToRomanNum(lvl);
        ItemMeta meta = item.getItemMeta().clone();
        meta.setCustomModelData(lvl);
        
        List<Component> lore = meta.lore();
        if (lore == null) {
            lore = new ArrayList<>(); // Initialize the lore if it's null
        } else {
            // Remove existing "Water Protection" lore
            lore.removeIf(loreString -> loreString.examinableName().startsWith("Water Protection"));
        }
        
        lore.add(Component.text(ChatColor.GRAY + "{name} {lvl}"
                .replace("{name}", "Water Protection")
                .replace("{lvl}", level)));
    
        meta.lore(lore); // Set the modified lore back to the item meta
        item.setItemMeta(meta);
        
        net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        stack.enchant(eimpl, lvl);
    }

    private static String numberToRomanNum(int lvl){
        if(lvl > 10){
            Bukkit.getLogger().severe("Cannot translate value higher than max enchantment value in Genesis {4}");
            return null;
        }else{
            switch(lvl){
                case 1 -> {
                    return "I";
                }
                case 2 -> {
                    return "II";
                }
                case 3 -> {
                    return "III";
                }
                case 4 -> {
                    return "IV";
                }
                case 5 -> {
                    return "V";
                }
                case 6 -> {
                    return "VI";
                }
                case 7 -> {
                    return "VII";
                }
                case 8 -> {
                    return "VIII";
                }
                case 9 -> {
                    return "IX";
                }
                case 10 -> {
                    return "X";
                }
                default -> {
                    return "I";
                }
            }
        }
    }
}
