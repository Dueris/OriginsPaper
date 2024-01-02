package me.dueris.genesismc.enchantments;

import me.dueris.genesismc.Bootstrap;
import me.dueris.genesismc.GenesisMC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.chat.BaseComponent;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry;
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
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

    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        boolean conflicts = false;
        boolean anyNull = true;
        if(e.getInventory().getFirstItem() != null && e.getInventory().getSecondItem() != null){
            if(this.containsWaterProt(e.getInventory().getFirstItem()) || this.containsWaterProt(e.getInventory().getSecondItem())){
                Enchantment waterProt = CraftEnchantment.minecraftToBukkit(eimpl);

                for(Enchantment possConf : e.getInventory().getFirstItem().getEnchantments().keySet()){
                    if(!conflicts){
                        if(!this.isCompatibleWith(((CraftEnchantment)possConf).getHandle())){
                            conflicts = true;
                            e.setResult(null);
                        }
                    }
                }
                for(Enchantment possConf : e.getInventory().getSecondItem().getEnchantments().keySet()){
                    if(!conflicts){
                        if(!this.isCompatibleWith(((CraftEnchantment)possConf).getHandle())){
                            conflicts = true;
                            e.setResult(null);
                        }
                    }
                }
            }
        }
        if(!conflicts && e.getInventory().getFirstItem() != null && e.getInventory().getSecondItem() != null){
            // begin anvil calculations. no conflicts and the result != null
            if(this.containsWaterProt(e.getInventory().getFirstItem()) || this.containsWaterProt(e.getInventory().getSecondItem())){
                boolean firstContains = this.containsWaterProt(e.getInventory().getFirstItem());
                boolean secondContains = this.containsWaterProt(e.getInventory().getSecondItem());
                if(firstContains && secondContains){
                    int firstlvl = e.getInventory().getFirstItem().getItemMeta().getCustomModelData();
                    int secondlvl = e.getInventory().getSecondItem().getItemMeta().getCustomModelData();
                    int finl = 1;
                    if(firstlvl > secondlvl){
                        finl = firstlvl;
                    } else if(firstlvl < secondlvl){
                        finl = secondlvl;
                    } else if(firstlvl == secondlvl){
                        finl = firstlvl + 1;
                    }
                    ItemStack itemStack = new ItemStack(e.getInventory().getFirstItem());
                    setWaterProtCustomEnchantLevel(finl, itemStack);
                    e.setResult(itemStack);
                } else if(firstContains && !secondContains){
                    int firstlvl = e.getInventory().getFirstItem().getItemMeta().getCustomModelData();
                    ItemStack itemStack = new ItemStack(e.getInventory().getFirstItem());
                    setWaterProtCustomEnchantLevel(firstlvl, itemStack);
                    e.setResult(itemStack);
                } else if(!firstContains && secondContains){
                    int secondlvl = e.getInventory().getSecondItem().getItemMeta().getCustomModelData();
                    ItemStack itemStack = new ItemStack(e.getInventory().getFirstItem());
                    setWaterProtCustomEnchantLevel(secondlvl, itemStack);
                    e.setResult(itemStack);
                }
            }
        }
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

        net.minecraft.world.item.ItemStack stack = CraftItemStack.unwrap(item);
        stack.enchant(BuiltInRegistries.ENCHANTMENT.get(new ResourceLocation("origins", "water_protection")), lvl);
    }

    public boolean containsWaterProt(ItemStack item){
        boolean hasModelData = false;
        boolean hasCorrectLore = false;
        if(item.getItemMeta().hasCustomModelData()){
            if(item.getItemMeta().getCustomModelData() == 1 || item.getItemMeta().getCustomModelData() == 2 || item.getItemMeta().getCustomModelData() == 3 || item.getItemMeta().getCustomModelData() == 4){
                hasModelData = true;
            }
        }
        if(item.getItemMeta().lore() != null){
            for(Component lore : item.getItemMeta().lore()){
                if(lore.asComponent().toString().contains("Water Protection")){
                    hasCorrectLore = true;
                }
            }
        }
        return hasCorrectLore && hasModelData;
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
