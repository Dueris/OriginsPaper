package me.dueris.genesismc.factory.powers.player;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static org.bukkit.Material.ENDER_PEARL;

public class EnderPearlThrow extends CraftPower implements Listener {

    @Override
    public void setActive(String tag, Boolean bool){
        if(powers_active.containsKey(tag)){
            powers_active.replace(tag, bool);
        }else{
            powers_active.put(tag, bool);
        }
    }

    

    @EventHandler
    public void teleportDamgeOff(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (throw_ender_pearl.contains(e.getPlayer()) && e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            e.setCancelled(true);
            p.teleport(e.getTo());
        }

    }

    @EventHandler
    public void onThrow(final PlayerInteractEvent e) {
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);

        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);

        Player p = e.getPlayer();
        if (throw_ender_pearl.contains(e.getPlayer())) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getItem() != null) {
                    if (e.getItem().equals(infinpearl)) {
                        if (p.getCooldown(ENDER_PEARL) == 0 && p.getGameMode() != GameMode.CREATIVE) {
                            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                                ConditionExecutor executor = new ConditionExecutor();
                                if(executor.check("condition", "conditions", p, origin, getPowerFile(), null, p)){
                                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), true);
                                    p.getInventory().addItem(infinpearl);
                                    Bukkit.getScheduler().runTaskLater(GenesisMC.getPlugin(), () -> {
                                        if (p.getInventory().getItemInMainHand().isSimilar(infinpearl)) ;
                                        p.getInventory().getItemInMainHand().setAmount(1);
                                    }, 1);
                                }else{
                                    if(!getPowerArray().contains(p)) return;
                    setActive(origin.getPowerFileFromType(getPowerFile()).getTag(), false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void RespawnPearl(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);

        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);
        pearl_meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if (throw_ender_pearl.contains(e.getPlayer())) {
            if (!p.getInventory().contains(infinpearl) && p.getGameMode() != GameMode.CREATIVE) {
                p.getInventory().addItem(infinpearl);
            }
        }
    }

    @EventHandler
    public void CancelDrop(PlayerDropItemEvent e) {
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);
        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);

        if (throw_ender_pearl.contains(e.getPlayer()))
            if (e.getItemDrop().getItemStack().isSimilar(infinpearl)) {
                e.setCancelled(true);
            }
    }

    @EventHandler
    public void onTransfer(InventoryClickEvent e) {
        if (e.getClick().isKeyboardClick()) {
            if (e.getView().getTopInventory().getType() == InventoryType.CRAFTING) return;
            if (e.getView().getBottomInventory().getItem(e.getHotbarButton()) != null) {
                ItemStack transferred = e.getView().getBottomInventory().getItem(e.getHotbarButton());
                if (transferred == null) return;
                if (transferred.getType().equals(Material.ENDER_PEARL)) {
                    ItemStack infinpearl = new ItemStack(ENDER_PEARL);
                    ItemMeta pearl_meta = infinpearl.getItemMeta();
                    pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
                    ArrayList<String> pearl_lore = new ArrayList<>();
                    pearl_meta.setUnbreakable(true);
                    pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                    pearl_meta.setLore(pearl_lore);
                    infinpearl.setItemMeta(pearl_meta);

                    if (transferred.isSimilar(infinpearl)) {
                        e.setCancelled(true);
                    }
                }
            }

            return;
        }
        if (e.getView().getTopInventory().getType() != InventoryType.CRAFTING) {
            if (e.getView().getTopInventory().getHolder() != null && e.getView().getTopInventory().getHolder().equals(e.getWhoClicked()))
                return;
            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType().equals(Material.ENDER_PEARL)) {
                ItemStack infinpearl = new ItemStack(ENDER_PEARL);
                ItemMeta pearl_meta = infinpearl.getItemMeta();
                pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
                ArrayList<String> pearl_lore = new ArrayList<>();
                pearl_meta.setUnbreakable(true);
                pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                pearl_meta.setLore(pearl_lore);
                infinpearl.setItemMeta(pearl_meta);

                if (e.getCurrentItem().isSimilar(infinpearl)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void RemovePearl(PlayerDeathEvent e) {
        Player p = e.getEntity();
        ItemStack infinpearl = new ItemStack(ENDER_PEARL);
        ItemMeta pearl_meta = infinpearl.getItemMeta();
        pearl_meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Teleport");
        ArrayList<String> pearl_lore = new ArrayList<>();
        pearl_meta.setUnbreakable(true);
        pearl_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        pearl_meta.setLore(pearl_lore);
        infinpearl.setItemMeta(pearl_meta);
        pearl_meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        if (throw_ender_pearl.contains(e.getPlayer())) {
            e.getDrops().remove(infinpearl);
            e.getDrops().add(new ItemStack(ENDER_PEARL));
        }

    }

    @Override
    public void run() {

    }

    @Override
    public String getPowerFile() {
        return "origins:ender_pearl_throw";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return throw_ender_pearl;
    }
}
