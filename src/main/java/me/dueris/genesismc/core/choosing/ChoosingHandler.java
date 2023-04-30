package me.dueris.genesismc.core.choosing;

import me.dueris.genesismc.api.events.choose.contents.core.origins.HumanContents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static org.bukkit.ChatColor.*;

public class ChoosingHandler implements Listener {

    @EventHandler
    public void OnChoose(InventoryClickEvent e){
        if (e.getView().getTitle().equalsIgnoreCase("Choosing Menu")) {
            //Human
            ItemStack human = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta human_meta = human.getItemMeta();
            human_meta.setDisplayName("Human");
            ArrayList<String> human_lore = new ArrayList<>();
            human_lore.add(WHITE + "Human Origin");
            human_meta.addEnchant(Enchantment.ARROW_INFINITE, 0, true);
            human_meta.setLore(human_lore);
            human.setItemMeta(human_meta);

            ItemStack ender = new ItemStack(Material.ENDER_PEARL);
            ItemMeta ender_meta = ender.getItemMeta();
            ender_meta.setDisplayName("Enderian");
            ArrayList<String> ender_lore = new ArrayList<>();
            ender_lore.add(LIGHT_PURPLE + "Enderman Origin");
            human_meta.addEnchant(Enchantment.ARROW_INFINITE, 0, true);
            ender_meta.setLore(ender_lore);
            ender.setItemMeta(ender_meta);

            ItemStack shulk = new ItemStack(Material.SHULKER_SHELL);
            ItemMeta shulk_meta = shulk.getItemMeta();
            shulk_meta.setDisplayName("Shulk");
            ArrayList<String> shulk_lore = new ArrayList<>();
            shulk_lore.add(LIGHT_PURPLE + "Shulker Origin");
            shulk_meta.setLore(shulk_lore);
            shulk.setItemMeta(shulk_meta);

            ItemStack spider = new ItemStack(Material.COBWEB);
            ItemMeta spider_meta = spider.getItemMeta();
            spider_meta.setDisplayName("Arachnid");
            ArrayList<String> spider_lore = new ArrayList<>();
            spider_lore.add(RED + "Spider Origin");
            spider_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            spider_meta.setLore(spider_lore);
            spider.setItemMeta(spider_meta);

            ItemStack creep = new ItemStack(Material.GUNPOWDER);
            ItemMeta creep_meta = creep.getItemMeta();
            creep_meta.setDisplayName("Creep");
            ArrayList<String> creep_lore = new ArrayList<>();
            creep_lore.add(GREEN + "Creeper Origin");
            creep_meta.setLore(creep_lore);
            creep.setItemMeta(creep_meta);

            ItemStack phantom = new ItemStack(Material.PHANTOM_MEMBRANE);
            ItemMeta phantom_meta = phantom.getItemMeta();
            phantom_meta.setDisplayName("Phantom");
            ArrayList<String> phantom_lore = new ArrayList<>();
            phantom_lore.add(BLUE + "Phantom Origin");
            phantom_meta.setLore(phantom_lore);
            phantom.setItemMeta(phantom_meta);

            ItemStack slime = new ItemStack(Material.SLIME_BALL);
            ItemMeta slime_meta = slime.getItemMeta();
            slime_meta.setDisplayName("Slimeling");
            ArrayList<String> slime_lore = new ArrayList<>();
            slime_lore.add(GREEN + "Slime Origin");
            slime_meta.setLore(slime_lore);
            slime.setItemMeta(slime_meta);

            ItemStack vex = new ItemStack(Material.IRON_SWORD);
            ItemMeta vex_meta = vex.getItemMeta();
            vex_meta.setDisplayName("Vexian");
            ArrayList<String> vex_lore = new ArrayList<>();
            vex_lore.add(AQUA + "Vex Origin");
            vex_meta.setLore(vex_lore);
            vex.setItemMeta(vex_meta);

            ItemStack blaze = new ItemStack(Material.BLAZE_POWDER);
            ItemMeta blaze_meta = blaze.getItemMeta();
            blaze_meta.setDisplayName("Blazeborn");
            ArrayList<String> blaze_lore = new ArrayList<>();
            blaze_lore.add(GOLD + "Blaze Origin");
            blaze_meta.setLore(blaze_lore);
            blaze.setItemMeta(blaze_meta);

            ItemStack star = new ItemStack(Material.NETHER_STAR);
            ItemMeta star_meta = star.getItemMeta();
            star_meta.setDisplayName("Starborne");
            ArrayList<String> star_lore = new ArrayList<>();
            star_lore.add(LIGHT_PURPLE + "Starborne Origin");
            star_meta.setLore(star_lore);
            star.setItemMeta(star_meta);
            Player player = (Player) e.getWhoClicked();
            if (e.getCurrentItem() != null && e.getCurrentItem().containsEnchantment(Enchantment.ARROW_INFINITE)) {
                if(e.getCurrentItem().equals(human)){

                }
                if(e.getCurrentItem().equals(ender)){

                }
                if(e.getCurrentItem().equals(shulk)){

                }
                if(e.getCurrentItem().equals(spider)){

                }
                if(e.getCurrentItem().equals(creep)){

                }
                if(e.getCurrentItem().equals(phantom)){

                }
                if(e.getCurrentItem().equals(slime)){

                }
                if(e.getCurrentItem().equals(vex)){

                }

            }
        }

    }

}
