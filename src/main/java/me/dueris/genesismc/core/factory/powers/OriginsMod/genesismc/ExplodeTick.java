package me.dueris.genesismc.core.factory.powers.OriginsMod.genesismc;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.dueris.genesismc.core.factory.powers.Powers.explode_tick;
import static me.dueris.genesismc.core.factory.powers.Powers.particle;

public class ExplodeTick implements Listener {
    private final HashMap<UUID, Long> cooldown;

    public ExplodeTick() {
        this.cooldown = new HashMap<>();
    }


    @EventHandler
    public void onShiftCreep(PlayerToggleSneakEvent e) {
        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()){
            Player p = e.getPlayer();
            if (explode_tick.contains(e.getPlayer()) && !p.isFlying() && !p.isGliding()) {

                cooldown.remove(p.getUniqueId());
                new BukkitRunnable() {
                    final Material block = e.getPlayer().getLocation().getBlock().getType();

                    @Override
                    public void run() {

                        if (p.isSneaking()) {
                            if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) > 3300)) {
                                if (p.isSneaking()) {
                                    cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                } else {
                                    this.cancel();
                                }

                            }

                            int power = Math.toIntExact((Long) origin.getPowerFileFromType("genesis:explode_tick").getModifier().get("power"));
                            int resistance = Math.toIntExact((Long) origin.getPowerFileFromType("genesis:explode_tick").getModifier().get("resistance"));
                            int charge = Math.toIntExact((Long) origin.getPowerFileFromType("genesis:explode_tick").getModifier().get("charge"));
                            boolean fire = (boolean) origin.getPowerFileFromType("genesis:explode_tick").getModifier().get("fire");
                            boolean break_blocks = (boolean) origin.getPowerFileFromType("genesis:explode_tick").getModifier().get("break_blocks");

                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 2, false, false, false));
                            if (!cooldown.containsKey(p.getUniqueId()) || (System.currentTimeMillis() - cooldown.get(p.getUniqueId()) >= 2900)) {

                                List<Entity> nearby = p.getNearbyEntities(2, 2, 2);
                                for (Entity tmp : nearby)
                                    if (tmp instanceof Damageable && tmp != p)
                                        ((Damageable) tmp).damage(15);
                                List<Entity> nearby2 = p.getNearbyEntities(3, 3, 3);
                                for (Entity tmp2 : nearby2)
                                    if (tmp2 instanceof Damageable && tmp2 != p)
                                        ((Damageable) tmp2).damage(10);
                                List<Entity> nearby3 = p.getNearbyEntities(5, 5, 5);
                                for (Entity tmp3 : nearby3)
                                    if (tmp3 instanceof Damageable && tmp3 != p)
                                        ((Damageable) tmp3).damage(5);
                                e.setCancelled(true);
                                cancel();

                                if(origin.getPowerFileFromType("genesis:explode_tick").getThunderModifier() != null){
                                    int power_thunder = (int) origin.getPowerFileFromType("genesis:explode_tick").getThunderModifier().get("power");
                                    int resistance_thunder = (int) origin.getPowerFileFromType("genesis:explode_tick").getThunderModifier().get("resistance");
                                    boolean fire_thunder = (boolean) origin.getPowerFileFromType("genesis:explode_tick").getThunderModifier().get("fire");
                                    boolean break_blocks_thunder = (boolean) origin.getPowerFileFromType("genesis:explode_tick").getThunderModifier().get("break_blocks");
                                    if (p.getWorld().isThundering()) {
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, resistance_thunder, true, false, false));
                                        p.getWorld().createExplosion(p.getLocation(), power_thunder, fire_thunder, break_blocks_thunder, p);
                                        p.teleportAsync(p.getLocation());
                                        p.damage(5);
                                        e.setCancelled(true);
                                        this.cancel();
                                    } else {
                                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, resistance, true, false, false));
                                        p.getWorld().createExplosion(p.getLocation(), (float) power, fire, break_blocks, p);
                                        cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                        p.teleportAsync(p.getLocation());
                                        p.damage(10);
                                        e.setCancelled(true);
                                        this.cancel();
                                    }
                                }else{
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, resistance, true, false, false));
                                    p.getWorld().createExplosion(p.getLocation(), power, fire, break_blocks, p);
                                    cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                    p.teleportAsync(p.getLocation());
                                    p.damage(10);
                                    e.setCancelled(true);
                                    this.cancel();
                                }

                            } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= charge)) {
                                p.sendActionBar(ChatColor.RED + "[]");
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6, 4, false, false, false));
                            } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= (charge * 3/4))) {
                                p.sendActionBar(ChatColor.YELLOW + "----");
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6, 3, false, false, false));
                            } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= (charge * 2/4))) {
                                p.sendActionBar(ChatColor.GREEN + "------");
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6, 2, false, false, false));
                            } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) <= (charge * 1/4))) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 6, 1, false, false, false));
                                p.sendActionBar(ChatColor.BLUE + "--------");
                            }


                        } else {
                            this.cancel();
                        }
                    }

                }.runTaskTimer(GenesisMC.getPlugin(), 0L, 5L);
            }
        }
    }


}
