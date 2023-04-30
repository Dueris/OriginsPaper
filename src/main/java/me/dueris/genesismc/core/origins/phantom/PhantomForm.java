package me.dueris.genesismc.core.origins.phantom;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.RED;

public class PhantomForm implements Listener {

    @EventHandler
    public void onKey(PlayerInteractEvent e) {
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
        Action action = e.getAction();

        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
        if (originid == 7300041) {

            if (e.getItem() != null) {
                if (e.getItem().isSimilar(spectatorswitch)) {
                    if (phantomid == 1) {
                        if(p.getGameMode() != GameMode.SPECTATOR) {

                            if(p.getFoodLevel() > 6){
                                p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 2);
                                p.sendActionBar(DARK_AQUA + "Activated Phantom Form");
                                p.setInvisible(true);
                                p.setSilent(true);
                                p.setCollidable(false);
                                p.setInvulnerable(false);
                                p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.8);

                            }else{
                                p.sendMessage(RED + "You must be able to sprint to switch forms");
                            }

                            CraftPlayer craftPlayer = (CraftPlayer) p;

                        }else{p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");}
                    } else if (phantomid == 2) {
                        if(p.getGameMode() != GameMode.SPECTATOR) {

                            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                            p.sendActionBar(DARK_AQUA + "Deactivated Phantom Form");
                            p.setInvisible(false);
                            p.setSilent(false);
                            p.setCollidable(true);
                            p.setInvulnerable(false);
                            p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.11);

                        }else{p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");}
                    } else {
                        p.sendMessage(RED + "Error: Switching could not be executed");
                    }
                    e.setCancelled(true);
                }
            }

        }

    }

        //replaced move handler with PhantomFormRunnable.java

    @EventHandler

    public void YLevelBedrockDetectPATCH(PlayerMoveEvent e){

        Player p = e.getPlayer();

            PersistentDataContainer data = p.getPersistentDataContainer();

            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);

            int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);

            //begin checks

        if (originid == 7300041) {

            if(phantomid == 2) {

                //in phantom form.

                //bug fix attempt 3 lol

                if(e.getTo().getY() <= -63.5){
                    final Location loc = new Location(p.getWorld(), e.getFrom().getX(), -63, e.getFrom().getZ(), e.getTo().getYaw(), e.getTo().getPitch());
                    p.teleportAsync(loc);
                    p.sendMessage("You are unable to go bellow Y level -64 while in Phantom Form");
                }

            }

        }

   

    }
    
  /*  @EventHandler
    public void ObsidianBedrockDetectPATCH(PlayerMoveEvent e){
        Player p = e.getPlayer();
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
            //begin checks
            if (originid == 7300041) {
                if(phantomid == 2) {
                    //in phantom form.
                    //bug fix attempt 3 lol
                    
                    if (e.getTo().getBlock().getType() == Material.OBSIDIAN || e.getTo().getBlock().getType() == Material.BEDROCK) {
                    final Location loc = new Location(p.getWorld(), e.getFrom().getX(), e.getFrom().getY(), e.getFrom().getZ(), e.getTo().getYaw(), e.getTo().getPitch());
                       p.teleportAsync(loc);
                       e.setCancelled(true);
                    }
            final Location eye = new Location(p.getWorld(), e.getTo().add(0, 1, 0).getX(), e.getTo().add(0, 1, 0).getY(), e.getTo().add(0, 1, 0).getZ(), e.getTo().getYaw(), e.getTo().getPitch());
            final Location eyef = new Location(p.getWorld(), e.getFrom().getX(), e.getFrom().getY(), e.getFrom().getZ(), e.getTo().getYaw(), e.getTo().getPitch());
            if (eye.getBlock().getType() == Material.OBSIDIAN || eye.getBlock().getType() == Material.BEDROCK) {
                p.teleportAsync(eyef);
                e.setCancelled(true);
            }
                    
          }

       }
    
    }

*/
    @EventHandler
    public void TPPATCH(PlayerTeleportEvent e){
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE){
            Player p = e.getPlayer();
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
            if (originid == 7300041) {
                if(phantomid == 2) {
                    e.setCancelled(true);
                }

            }
        }
    }


    @EventHandler
    public void CancelSpectate(PlayerStartSpectatingEntityEvent e){
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
        if (originid == 7300041) {
            if(phantomid == 2) {
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
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

        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 7300041) {
            if (e.getItemDrop().getItemStack().isSimilar(spectatorswitch)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        ItemStack spectatorswitch = new ItemStack(Material.PHANTOM_MEMBRANE);
        ItemMeta switch_meta = spectatorswitch.getItemMeta();
        switch_meta.setDisplayName(GRAY + "Phantom Form");
        ArrayList<String> pearl_lore = new ArrayList();
        switch_meta.setUnbreakable(true);
        switch_meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        switch_meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS);
        switch_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        switch_meta.setLore(pearl_lore);
        spectatorswitch.setItemMeta(switch_meta);

        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (e.getDrops().contains(spectatorswitch)) {
                e.getDrops().remove(spectatorswitch);
            }
    }
}
