package me.dueris.genesismc.core.factory.powers.entity;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorldBorder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

import static me.dueris.genesismc.core.factory.powers.Powers.phantomize;
import static org.bukkit.ChatColor.*;

public class Phantomized extends BukkitRunnable implements Listener {
    public static void initializePhantomOverlay(Player player) {
        CraftWorldBorder border = (CraftWorldBorder) Bukkit.createWorldBorder();
        border.setCenter(player.getWorld().getWorldBorder().getCenter());
        border.setSize(player.getWorld().getWorldBorder().getSize());
        border.setWarningDistance(999999999);
        player.setWorldBorder(border);
    }

    public static void deactivatePhantomOverlay(Player player) {
        player.setWorldBorder(player.getWorld().getWorldBorder());
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {

            PersistentDataContainer data = p.getPersistentDataContainer();
            int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);

            if (phantomid == 2) {
                if (phantomize.contains(OriginPlayer.getOriginTag(p))) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 0, false, false, false));
                    if ((p.getLocation().add(0.55F, 0, 0.55F).getBlock().isSolid() ||
                            p.getLocation().add(0.55F, 0, 0).getBlock().isSolid() ||
                            p.getLocation().add(0, 0, 0.55F).getBlock().isSolid() ||
                            p.getLocation().add(-0.55F, 0, -0.55F).getBlock().isSolid() ||
                            p.getLocation().add(0, 0, -0.55F).getBlock().isSolid() ||
                            p.getLocation().add(-0.55F, 0, 0).getBlock().isSolid() ||
                            p.getLocation().add(0.55F, 0, -0.55F).getBlock().isSolid() ||
                            p.getLocation().add(-0.55F, 0, 0.55F).getBlock().isSolid() ||
                            p.getLocation().add(0, 0.5, 0).getBlock().isSolid() ||

                            p.getEyeLocation().add(0.55F, 0, 0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(0.55F, 0, 0).getBlock().isSolid() ||
                            p.getEyeLocation().add(0, 0, 0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(-0.55F, 0, -0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(0, 0, -0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(-0.55F, 0, 0).getBlock().isSolid() ||
                            p.getEyeLocation().add(0.55F, 0, -0.55F).getBlock().isSolid() ||
                            p.getEyeLocation().add(-0.55F, 0, 0.55F).getBlock().isSolid())
                    ) {
                        //can form
                        if (p.isInsideVehicle()) return;
                        p.setCollidable(false);
                        p.setGameMode(GameMode.SPECTATOR);
                        p.setFlying(true);
                        p.setFlySpeed(0.05F);

                    } else {
                        if (p.getGameMode().equals(GameMode.SPECTATOR)) {
                            if (p.getPreviousGameMode().equals(GameMode.CREATIVE)) {
                                p.setGameMode(p.getPreviousGameMode());
                                p.setFlying(true);
                            } else {
                                p.setGameMode(p.getPreviousGameMode());
                                if (p.isOnGround()) ;
                                p.setFlying(false);
                            }

                        }
                    }

                    //code for if player is in "Phantom Form"
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.085);
                    initializePhantomOverlay(p);

                    Random random = new Random();
                    int r = random.nextInt(650);
                    if (!p.isSwimming()) {
                        if (r < 10) {
                            if (p.getFoodLevel() > 1) {
                                int foodamt = p.getFoodLevel();
                                p.setFoodLevel(foodamt - 1);
                            }
                        }
                    }
                } else {
                    deactivatePhantomOverlay(p);
                    p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.10000000149011612F);
                }
            } else {
                deactivatePhantomOverlay(p);
                p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.10000000149011612F);
            }
        }
    }

    @EventHandler
    public void RemoveCold(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
            if (phantomize.contains(OriginPlayer.getOriginTag(p)) && phantomid == 2) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.FREEZE)) {
                    e.setDamage(0);
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
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
        if (phantomize.contains(OriginPlayer.getOriginTag(e.getPlayer()))) {
            e.getPlayer().getInventory().addItem(spectatorswitch);
        }
    }

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

        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
        if (OriginPlayer.getOriginTag(e.getPlayer()).equalsIgnoreCase("genesis:origin-phantom")) {

            if (e.getItem() != null) {
                if (e.getItem().isSimilar(spectatorswitch)) {
                    if (phantomid == 1) {
                        if (p.getGameMode() != GameMode.SPECTATOR) {

                            if (p.getFoodLevel() > 6) {
                                p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 2);
                                p.sendActionBar(DARK_AQUA + "Activated Phantom Form");
                                p.setSilent(true);
                                p.setCollidable(false);

                            } else {
                                p.sendMessage(RED + "You must be able to sprint to switch forms");
                            }

                        } else {
                            p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");
                        }
                    } else if (phantomid == 2) {
                        if (p.getGameMode() != GameMode.SPECTATOR) {

                            p.getPersistentDataContainer().set(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER, 1);
                            p.sendActionBar(DARK_AQUA + "Deactivated Phantom Form");
                            p.setSilent(false);
                            p.setCollidable(true);

                        } else {
                            p.sendMessage(ChatColor.RED + "You are unable to switch forms while inside a block or in spectator mode.");
                        }
                    } else {
                        p.sendMessage(RED + "Error: Switching could not be executed");
                    }
                    e.setCancelled(true);
                }
            }

        }

    }

    @EventHandler

    public void YLevelBedrockDetectPATCH(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
        //begin checks
        if (OriginPlayer.getOriginTag(e.getPlayer()).equalsIgnoreCase("genesis:origin-phantom")) {
            if (phantomid == 2) {
                //in phantom form.
                //bug fix attempt 3 lol
                if (e.getTo().getY() <= -63.5) {
                    final Location loc = new Location(p.getWorld(), e.getFrom().getX(), e.getFrom().getY(), e.getFrom().getZ(), e.getTo().getYaw(), e.getTo().getPitch());
                    p.teleportAsync(loc);
                    p.sendMessage("You are unable to go bellow Y level -64 while in Phantom Form");
                }
            }
        }

    }

    @EventHandler
    public void TPPATCH(PlayerTeleportEvent e) {
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            Player p = e.getPlayer();
            PersistentDataContainer data = p.getPersistentDataContainer();
            int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
            if (OriginPlayer.getOriginTag(p).equalsIgnoreCase("genesis:origin-phantom")) {
                if (phantomid == 2) {
                    e.setCancelled(true);
                }

            }
        }
    }


    @EventHandler
    public void CancelSpectate(PlayerStartSpectatingEntityEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int phantomid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "in-phantomform"), PersistentDataType.INTEGER);
        if (OriginPlayer.getOriginTag(e.getPlayer()).equalsIgnoreCase("genesis:origin-phantom")) {
            if (phantomid == 2) {
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
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

        if (OriginPlayer.getOriginTag(e.getPlayer()).equalsIgnoreCase("genesis:origin-phantom")) {
            if (e.getItemDrop().getItemStack().isSimilar(spectatorswitch)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
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
        e.getDrops().remove(spectatorswitch);
    }

}
