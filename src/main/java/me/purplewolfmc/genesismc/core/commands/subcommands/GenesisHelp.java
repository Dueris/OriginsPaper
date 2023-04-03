package me.purplewolfmc.genesismc.core.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.purplewolfmc.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.LIGHT_PURPLE;

public class GenesisHelp extends SubCommand implements Listener {
  @Override
  public String getName() {
    return "help";
  }

  @Override
  public String getDescription() {
    return "opens menu for your origin";
  }

  @Override
  public String getSyntax() {
    return "/origins help";
  }

  @EventHandler
  public void onClickHelp(InventoryClickEvent e){
    Player p = (Player) e.getWhoClicked();
    if (e.getCurrentItem() != null) {
    if (e.getView().getTitle().equalsIgnoreCase(ChatColor.BLACK + "Help")) {
      e.setCancelled(true);
      if (e.getCurrentItem().getType() == Material.BARRIER) {
        p.closeInventory();
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_GUITAR, 10, 9);
        e.setCancelled(true);
      }
    }
    }
  }

  @Override
  public void perform(Player p, String[] args) {
    if(p.getScoreboardTags().contains("chosen")){
      if(p.getScoreboardTags().contains("human")){
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
        Inventory humangui = Bukkit.createInventory(p, 54, ChatColor.BLACK + "Help");

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack next = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack human = new ItemStack(Material.PLAYER_HEAD);
        ItemStack air = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack human_origin_info = new ItemStack(Material.FILLED_MAP);
        ItemStack blank = new ItemStack(Material.PAPER);
        



        ItemMeta human_origin_info_meta = human_origin_info.getItemMeta();
        human_origin_info_meta.setDisplayName("NOTHING");
        ArrayList<String> human_origin_info_lore = new ArrayList<>();
        human_origin_info_lore.add(ChatColor.WHITE + "You have no powers");
        human_origin_info_meta.setLore(human_origin_info_lore);
        human_origin_info.setItemMeta(human_origin_info_meta);

        ItemMeta close_meta = close.getItemMeta();
        close_meta.setDisplayName(ChatColor.RED + "Close");
        ArrayList<String> close_lore = new ArrayList<>();
        close_lore.add(ChatColor.RED + "Cancel Choosing");
        close_meta.setLore(close_lore);
        close.setItemMeta(close_meta);

        ItemMeta human_meta = human.getItemMeta();
        human_meta.setDisplayName("Human");
        ArrayList<String> human_lore = new ArrayList<>();
        human_lore.add(ChatColor.WHITE + "Human Origin");
        human_meta.setLore(human_lore);
        human.setItemMeta(human_meta);


        ItemStack[] humangui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, human, air, air, air, air, air, air, blank, blank, human_origin_info, blank, blank, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, next, air, air, air, air};
        humangui.setContents(humangui_items);
        p.openInventory(humangui);
      }
      if(p.getScoreboardTags().contains("enderian")){
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);
        Inventory endergui = Bukkit.createInventory(p, 54, ChatColor.BLACK + "Help");

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemStack next = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack ender = new ItemStack(Material.ENDER_PEARL);
        ItemStack air = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemStack ender_origin_info1 = new ItemStack(Material.FILLED_MAP);
        ItemStack blank = new ItemStack(Material.PAPER);
        
        ItemStack ender_origin_info2 = new ItemStack(Material.FILLED_MAP);
        ItemStack ender_origin_info3 = new ItemStack(Material.FILLED_MAP);
        ItemStack ender_origin_info4 = new ItemStack(Material.FILLED_MAP);
        ItemStack ender_origin_info5 = new ItemStack(Material.FILLED_MAP);



        ItemMeta ender_origin_info1_meta = ender_origin_info1.getItemMeta();
        ender_origin_info1_meta.setDisplayName("Teleport");
        ArrayList<String> ender_origin_info1_lore = new ArrayList<>();
        ender_origin_info1_lore.add(ChatColor.WHITE + "You have an infinite ender pearl that alows you to teleport with no damage");
        ender_origin_info1_meta.setLore(ender_origin_info1_lore);
        ender_origin_info1.setItemMeta(ender_origin_info1_meta);

        ItemMeta ender_origin_info2_meta = ender_origin_info2.getItemMeta();
        ender_origin_info2_meta.setDisplayName("Hydrophobia");
        ArrayList<String> ender_origin_info2_lore = new ArrayList<>();
        ender_origin_info2_lore.add(ChatColor.WHITE + "You will take damage while in contact with water");
        ender_origin_info2_meta.setLore(ender_origin_info2_lore);
        ender_origin_info2.setItemMeta(ender_origin_info2_meta);

        ItemMeta ender_origin_info3_meta = ender_origin_info3.getItemMeta();
        ender_origin_info3_meta.setDisplayName("Slender Body");
        ArrayList<String> ender_origin_info3_lore = new ArrayList<>();
        ender_origin_info3_lore.add(ChatColor.WHITE + "You can reach blocks and entities further away");
        ender_origin_info3_meta.setLore(ender_origin_info3_lore);
        ender_origin_info3.setItemMeta(ender_origin_info3_meta);

        ItemMeta ender_origin_info4_meta = ender_origin_info4.getItemMeta();
        ender_origin_info4_meta.setDisplayName("Scared of Gourds");
        ArrayList<String> ender_origin_info4_lore = new ArrayList<>();
        ender_origin_info4_lore.add(ChatColor.WHITE + "You take damage from pumpkins");
        ender_origin_info4_meta.setLore(ender_origin_info4_lore);
        ender_origin_info4.setItemMeta(ender_origin_info4_meta);

        ItemMeta ender_origin_info5_meta = ender_origin_info5.getItemMeta();
        ender_origin_info5_meta.setDisplayName("Bearer of Pearls");
        ArrayList<String> ender_origin_info5_lore = new ArrayList<>();
        ender_origin_info5_lore.add(ChatColor.WHITE + "You always drop 0-2 ender pearls upon death");
        ender_origin_info5_meta.setLore(ender_origin_info5_lore);
        ender_origin_info5.setItemMeta(ender_origin_info5_meta);

        ItemMeta close_meta = close.getItemMeta();
        close_meta.setDisplayName(ChatColor.RED + "Close");
        ArrayList<String> close_lore = new ArrayList<>();
        close_lore.add(ChatColor.RED + "Cancel Choosing");
        close_meta.setLore(close_lore);
        close.setItemMeta(close_meta);

        ItemMeta ender_meta = ender.getItemMeta();
        ender_meta.setDisplayName("Enderian");
        ArrayList<String> ender_lore = new ArrayList<>();
        ender_lore.add(LIGHT_PURPLE + "Enderman Origin");
        ender_meta.setLore(ender_lore);
        ender.setItemMeta(ender_meta);


        ItemStack[] endergui_items = {close, air, air, air, orb, air, air, air, close, air, air, air, air, ender, air, air, air, air, air, air, ender_origin_info1, ender_origin_info2, ender_origin_info3, ender_origin_info4, ender_origin_info5, air, air, air, air, blank, blank, blank, blank, blank, air, air, air, air, air, air, air, air, air, air, air, air, air, air, air, next, air, air, air, air};
        endergui.setContents(endergui_items);
        p.openInventory(endergui);
      }
    }else{
      p.sendMessage(ChatColor.RED + "You have not chosen an origin yet.");
    }
  }
}
