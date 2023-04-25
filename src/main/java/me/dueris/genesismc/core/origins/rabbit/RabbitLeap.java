package me.dueris.genesismc.core.origins.rabbit;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class RabbitLeap implements Listener, CommandExecutor {

    private static HashMap<UUID, Integer> cooldownBefore = new HashMap<>();
    private static HashMap<UUID, Long> cooldownAfter = new HashMap<>();
    private static ArrayList<UUID> inAir = new ArrayList<>();
    private static ArrayList<UUID> leapToggle = new ArrayList<>();


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (p.hasPermission("genesismc.origins.cmd.leaptoggle")) {
                if (originid == 5308033) {
                    if (leapToggle.contains(p.getUniqueId())) {
                        leapToggle.remove(p.getUniqueId());
                        p.sendMessage(ChatColor.GREEN + "Leap enabled.");
                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                    } else {
                        leapToggle.add(p.getUniqueId());
                        p.sendMessage(ChatColor.RED + "Leap disabled.");
                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "You need to be the rabbit origin to use this command.");
                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3f, 1);
                }
            } else {
                p.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3f, 1);
            }
        } else if (sender instanceof ConsoleCommandSender) {
            sender.getServer().getConsoleSender().sendMessage(ChatColor.RED + "This is a player only command.");
        }
        return true;
    }

    @EventHandler
    public void onRabbitLeap(PlayerToggleSneakEvent e) {
        PersistentDataContainer data = e.getPlayer().getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 5308033) {
            Player p = e.getPlayer();
            if (p.isSneaking()) return;
            if (!p.isOnGround()) return;
            if (cooldownAfter.containsKey(p.getUniqueId())) return;
            if (leapToggle.contains(p.getUniqueId())) return;

            cooldownBefore.put(p.getUniqueId(), 0);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (p.isSneaking()) {
                        if (cooldownBefore.get(p.getUniqueId()) == 2) {
                            p.sendActionBar(ChatColor.RED + "|||");
                        } else if (cooldownBefore.get(p.getUniqueId()) == 4) {
                            p.sendActionBar(ChatColor.RED + "|||||");
                        } else if (cooldownBefore.get(p.getUniqueId()) == 6) {
                            p.sendActionBar(ChatColor.YELLOW + "|||||||");
                        } else if (cooldownBefore.get(p.getUniqueId()) == 8) {
                            p.sendActionBar(ChatColor.YELLOW + "|||||||||");
                        } else if (cooldownBefore.get(p.getUniqueId()) >= 10) {
                            p.sendActionBar(ChatColor.GREEN + "|||||||||||");
                            cooldownBefore.replace(p.getUniqueId(), 9);
                        }
                        cooldownBefore.replace(p.getUniqueId(), cooldownBefore.get(p.getUniqueId()) + 1);
                    } else {
                        cooldownAfter.put(p.getUniqueId(), System.currentTimeMillis());
                        inAir.add(p.getUniqueId());
                        p.setVelocity(p.getLocation().getDirection().multiply(1.5 + cooldownBefore.get(p.getUniqueId())/10));
                        cooldownBefore.remove(p.getUniqueId());
                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);
                        doLeap(p);
                        this.cancel();
                    }
                }
            }.runTaskTimer(GenesisMC.getPlugin(), 0L, 2L);
        }
    }

    //This solved "java.lang.NoClassDefFoundError"
    public static void doLeap(Player p) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (cooldownAfter.containsKey(p.getUniqueId())) {
                    if (System.currentTimeMillis() - cooldownAfter.get(p.getUniqueId()) >= 0) {
                        p.sendActionBar(ChatColor.RED + "|||||||||");
                    } if (System.currentTimeMillis() - cooldownAfter.get(p.getUniqueId()) >= 2500) {
                        p.sendActionBar(ChatColor.RED + "|||||||");
                    } if (System.currentTimeMillis() - cooldownAfter.get(p.getUniqueId()) >= 5000) {
                        p.sendActionBar(ChatColor.YELLOW + "|||||");
                    } if (System.currentTimeMillis() - cooldownAfter.get(p.getUniqueId()) >= 7500) {
                        p.sendActionBar(ChatColor.YELLOW + "|||");
                    } if (System.currentTimeMillis() - cooldownAfter.get(p.getUniqueId()) >= 10000) {
                        cooldownAfter.remove(p.getUniqueId());
                        p.sendActionBar(ChatColor.GREEN + "-");
                        inAir.remove(p.getUniqueId());
                        p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 2);

                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(GenesisMC.getPlugin(), 0L, 10L);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (cooldownAfter.containsKey(p.getUniqueId())) {p.playSound(p.getLocation(), Sound.BLOCK_SCAFFOLDING_HIT, 1, 2);}
                else {this.cancel();}
            }
        } .runTaskTimer(GenesisMC.getPlugin(), 0L, 50L);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;

        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 5308033) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setDamage(e.getDamage() - 4);

                if (inAir.contains(p.getUniqueId())) {
                    e.setCancelled(true);
                    inAir.remove(p.getUniqueId());
                }
            }
        }
    }
}
