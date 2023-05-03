package me.dueris.genesismc.core.items;

import org.bukkit.ChatColor;

import org.bukkit.Material;

import org.bukkit.enchantments.Enchantment;

import org.bukkit.inventory.ItemFlag;

import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import java.util.List;

public class Items implements Listener{

    

      

        

        

        

        

     
        

        

            
    @EventHandler
    public void oncraftattempt(PrepareItemCraftEvent e){
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);

        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);
        
        ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);

        ItemMeta switch_meta = spectatorswitch.getItemMeta();

        switch_meta.setDisplayName(GRAY + "Phantom Form");

        ArrayList<String> pearl_lore = new ArrayList();

        switch_meta.setUnbreakable(true);

        switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);

        switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);

        switch_meta.setLore(pearl_lore);

        switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        spectatorswitch.setItemMeta(switch_meta);
        if(e.getInventory().contains(infinpearl) || e.getInventory().contains(spectatorswitch)){
            e.getInventory().setResult(null);
        }
    }


      



}
