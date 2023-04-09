package me.purplewolfmc.genesismc.core.origins.creep;

import me.purplewolfmc.genesismc.core.GenesisMC;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.management.timer.Timer;
import java.sql.Time;
import java.util.HashMap;
import java.util.UUID;

import static java.lang.System.currentTimeMillis;

public class CreepMain implements Listener {

    private final HashMap<UUID, Long> cooldown;

    public CreepMain() {
        this.cooldown = new HashMap<>();
    }

    @EventHandler
    public void onShiftCreep(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 1407068) {
            cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if (p.isSneaking() && !p.isJumping()) {
                            if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) > 1400)) {
                                cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                TNTPrimed t = p.getWorld().spawn(p.getLocation(), TNTPrimed.class);
                                t.setFuseTicks(0);
                                p.teleportAsync(p.getLocation());
                                p.setSneaking(false);
                                cancel();
                            }
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 2, false, false, false));
                        } else {
                            //if the cooldown is not over, send the p a message
                        }

                    }
                }.runTaskTimer(GenesisMC.getPlugin(), 0L, 5L);
            }
        }
    }

