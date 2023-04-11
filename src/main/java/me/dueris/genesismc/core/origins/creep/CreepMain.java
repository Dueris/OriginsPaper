package me.dueris.genesismc.core.origins.creep;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.management.timer.Timer;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.lang.System.currentTimeMillis;

public class CreepMain implements Listener {

    private final HashMap<UUID, Long> cooldown;
    private final HashMap<UUID, Long> explodecooldown;

    public CreepMain() {
        this.explodecooldown = new HashMap<>();
        this.cooldown = new HashMap<>();
    }

    @EventHandler
    public void onShiftCreep(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 2356555) {
            new BukkitRunnable() {
                Material block = e.getPlayer().getLocation().getBlock().getType();

                @Override
                public void run() {

                        if (p.isSneaking() && !p.isJumping()) {

                                if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) > 3500)) {
                                    if (originid == 2356555) {
                                        cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                        p.addScoreboardTag("exploding");
                                        explodecooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                        if (p.getWorld().isThundering()) {
                                            p.getWorld().createExplosion(p.getLocation(), 6);
                                        } else {
                                            p.getWorld().createExplosion(p.getLocation(), 3);
                                        }
                                        p.teleportAsync(p.getLocation());
                                    }
                                    new BukkitRunnable() {

                                        @Override
                                        public void run() {

                                                p.removeScoreboardTag("exploding");
                                                this.cancel();

                                        }
                                    }.runTaskLater(GenesisMC.getPlugin(), 60L);
                                        if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)) {
                                            float curhealth = (float) p.getHealth();
                                            float helemt_modifier = 0;
                                            float chestplate_modifier = 0;
                                            float leggins_modifier = 0;
                                            float boots_modifier = 0;
                                            float prot1 = (float) 0.2;
                                            float prot2 = (float) 0.4;
                                            float prot3 = (float) 0.6;
                                            float prot4 = (float) 0.9;
                                            if (p.getInventory().getHelmet() != null) {
                                                if (p.getInventory().getHelmet().getItemMeta().hasEnchant(Enchantment.PROTECTION_EXPLOSIONS)) {
                                                    if (p.getEquipment().getHelmet().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 1) {
                                                        helemt_modifier = prot1;
                                                    } else if (p.getEquipment().getHelmet().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 2) {
                                                        helemt_modifier = prot2;
                                                    } else if (p.getEquipment().getHelmet().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 3) {
                                                        helemt_modifier = prot3;
                                                    } else if (p.getEquipment().getHelmet().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 4) {
                                                        helemt_modifier = prot4;
                                                    } else {
                                                        helemt_modifier = 0;
                                                    }
                                                }
                                            }
                                            if (p.getInventory().getChestplate() != null) {
                                                if (p.getInventory().getChestplate().getItemMeta().hasEnchant(Enchantment.PROTECTION_EXPLOSIONS)) {
                                                    if (p.getEquipment().getChestplate().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 1) {
                                                        chestplate_modifier = prot1;
                                                    } else if (p.getEquipment().getChestplate().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 2) {
                                                        chestplate_modifier = prot2;
                                                    } else if (p.getEquipment().getChestplate().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 3) {
                                                        chestplate_modifier = prot3;
                                                    } else if (p.getEquipment().getChestplate().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 4) {
                                                        chestplate_modifier = prot4;
                                                    } else {
                                                        chestplate_modifier = 0;
                                                    }
                                                }
                                            }
                                            if (p.getInventory().getLeggings() != null) {
                                                if (p.getInventory().getLeggings().getItemMeta().hasEnchant(Enchantment.PROTECTION_EXPLOSIONS)) {
                                                    if (p.getEquipment().getLeggings().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 1) {
                                                        leggins_modifier = prot1;
                                                    } else if (p.getEquipment().getLeggings().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 2) {
                                                        leggins_modifier = prot2;
                                                    } else if (p.getEquipment().getLeggings().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 3) {
                                                        leggins_modifier = prot3;
                                                    } else if (p.getEquipment().getLeggings().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 4) {
                                                        leggins_modifier = prot4;
                                                    } else {
                                                        leggins_modifier = 0;
                                                    }
                                                }
                                            }
                                            if (p.getInventory().getBoots() != null) {
                                                if (p.getInventory().getBoots().getItemMeta().hasEnchant(Enchantment.PROTECTION_EXPLOSIONS)) {
                                                    if (p.getEquipment().getBoots().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 1) {
                                                        boots_modifier = prot1;
                                                    } else if (p.getEquipment().getBoots().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 2) {
                                                        boots_modifier = prot2;
                                                    } else if (p.getEquipment().getBoots().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 3) {
                                                        boots_modifier = prot3;
                                                    } else if (p.getEquipment().getBoots().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 4) {
                                                        boots_modifier = prot4;
                                                    } else {
                                                        boots_modifier = 0;
                                                    }
                                                }
                                            }
                                            float basedamage = 6 - helemt_modifier - chestplate_modifier - leggins_modifier - boots_modifier;


                                            if (p.getHealth() >= basedamage && p.getHealth() != 0 && p.getHealth() - basedamage != 0) {
                                                p.damage(0.0000001);
                                                p.setHealth(curhealth - basedamage);

                                                Random random = new Random();

                                                int r = random.nextInt(3);
                                                if (r == 1) {
                                                    if (p.getInventory().getHelmet() != null) {
                                                        int heldur = p.getEquipment().getHelmet().getDurability();
                                                        p.getEquipment().getHelmet().setDurability((short) (heldur + 3));
                                                    }
                                                    if (p.getInventory().getChestplate() != null) {
                                                        int chestdur = p.getEquipment().getChestplate().getDurability();
                                                        p.getEquipment().getChestplate().setDurability((short) (chestdur + 3));
                                                    }
                                                    if (p.getInventory().getLeggings() != null) {
                                                        int legdur = p.getEquipment().getLeggings().getDurability();
                                                        p.getEquipment().getLeggings().setDurability((short) (legdur + 3));
                                                    }
                                                    if (p.getInventory().getBoots() != null) {
                                                        int bootdur = p.getEquipment().getBoots().getDurability();
                                                        p.getEquipment().getBoots().setDurability((short) (bootdur + 3));
                                                    }

                                                }
                                            } else if (p.getHealth() <= basedamage && p.getHealth() != 0) {
                                                if (originid == 2356555) {
                                                    p.setHealth(curhealth - curhealth);
                                                }
                                            }
                                        }
                                    if (originid == 2356555) {
                                        List<Entity> nearby = p.getNearbyEntities(2, 2, 2);
                                        for (Entity tmp : nearby)
                                            if (tmp instanceof Damageable)
                                                ((Damageable) tmp).damage(15);
                                        List<Entity> nearby2 = p.getNearbyEntities(3, 3, 3);
                                        for (Entity tmp2 : nearby2)
                                            if (tmp2 instanceof Damageable)
                                                ((Damageable) tmp2).damage(10);
                                        List<Entity> nearby3 = p.getNearbyEntities(5, 5, 5);
                                        for (Entity tmp3 : nearby3)
                                            if (tmp3 instanceof Damageable)
                                                ((Damageable) tmp3).damage(5);
                                        e.setCancelled(true);
                                        cancel();
                                    }



                                }

                                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 2, false, false, false));
                                    if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= 2800)) {
                                        p.sendActionBar(ChatColor.RED + "--");
                                    } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= 2100)) {
                                        p.sendActionBar(ChatColor.YELLOW + "----");
                                    } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= 1400)) {
                                        p.sendActionBar(ChatColor.GREEN + "------");
                                    } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) <= 700)) {
                                        p.sendActionBar(ChatColor.BLUE + "--------");
                                    }



                        } else if (block == Material.FIRE) {

                                if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) > 3500)) {
                                    if (originid == 2356555) {
                                        cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                        explodecooldown.put(p.getUniqueId(), System.currentTimeMillis());
                                        if (p.getWorld().isThundering()) {
                                            p.getWorld().createExplosion(p.getLocation(), 6);
                                        } else {
                                            p.getWorld().createExplosion(p.getLocation(), 3);
                                        }
                                        p.teleportAsync(p.getLocation());
                                    }

                                        if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)) {
                                            float curhealth = (float) p.getHealth();
                                            float helemt_modifier = 0;
                                            float chestplate_modifier = 0;
                                            float leggins_modifier = 0;
                                            float boots_modifier = 0;
                                            float prot1 = (float) 0.2;
                                            float prot2 = (float) 0.4;
                                            float prot3 = (float) 0.6;
                                            float prot4 = (float) 0.9;
                                            if (p.getInventory().getHelmet() != null) {
                                                if (p.getInventory().getHelmet().getItemMeta().hasEnchant(Enchantment.PROTECTION_EXPLOSIONS)) {
                                                    if (p.getEquipment().getHelmet().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 1) {
                                                        helemt_modifier = prot1;
                                                    } else if (p.getEquipment().getHelmet().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 2) {
                                                        helemt_modifier = prot2;
                                                    } else if (p.getEquipment().getHelmet().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 3) {
                                                        helemt_modifier = prot3;
                                                    } else if (p.getEquipment().getHelmet().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 4) {
                                                        helemt_modifier = prot4;
                                                    } else {
                                                        helemt_modifier = 0;
                                                    }
                                                }
                                            }
                                            if (p.getInventory().getChestplate() != null) {
                                                if (p.getInventory().getChestplate().getItemMeta().hasEnchant(Enchantment.PROTECTION_EXPLOSIONS)) {
                                                    if (p.getEquipment().getChestplate().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 1) {
                                                        chestplate_modifier = prot1;
                                                    } else if (p.getEquipment().getChestplate().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 2) {
                                                        chestplate_modifier = prot2;
                                                    } else if (p.getEquipment().getChestplate().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 3) {
                                                        chestplate_modifier = prot3;
                                                    } else if (p.getEquipment().getChestplate().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 4) {
                                                        chestplate_modifier = prot4;
                                                    } else {
                                                        chestplate_modifier = 0;
                                                    }
                                                }
                                            }
                                            if (p.getInventory().getLeggings() != null) {
                                                if (p.getInventory().getLeggings().getItemMeta().hasEnchant(Enchantment.PROTECTION_EXPLOSIONS)) {
                                                    if (p.getEquipment().getLeggings().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 1) {
                                                        leggins_modifier = prot1;
                                                    } else if (p.getEquipment().getLeggings().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 2) {
                                                        leggins_modifier = prot2;
                                                    } else if (p.getEquipment().getLeggings().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 3) {
                                                        leggins_modifier = prot3;
                                                    } else if (p.getEquipment().getLeggings().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 4) {
                                                        leggins_modifier = prot4;
                                                    } else {
                                                        leggins_modifier = 0;
                                                    }
                                                }
                                            }
                                            if (p.getInventory().getBoots() != null) {
                                                if (p.getInventory().getBoots().getItemMeta().hasEnchant(Enchantment.PROTECTION_EXPLOSIONS)) {
                                                    if (p.getEquipment().getBoots().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 1) {
                                                        boots_modifier = prot1;
                                                    } else if (p.getEquipment().getBoots().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 2) {
                                                        boots_modifier = prot2;
                                                    } else if (p.getEquipment().getBoots().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 3) {
                                                        boots_modifier = prot3;
                                                    } else if (p.getEquipment().getBoots().getEnchantmentLevel(Enchantment.PROTECTION_EXPLOSIONS) == 4) {
                                                        boots_modifier = prot4;
                                                    } else {
                                                        boots_modifier = 0;
                                                    }
                                                }
                                            }
                                            float basedamage = 6 - helemt_modifier - chestplate_modifier - leggins_modifier - boots_modifier;


                                            if (p.getHealth() >= basedamage && p.getHealth() != 0 && p.getHealth() - basedamage != 0) {
                                                if (originid == 2356555) {
                                                    p.damage(0.0000001);
                                                    p.setHealth(curhealth - basedamage);
                                                }

                                                Random random = new Random();

                                                int r = random.nextInt(3);
                                                if (r == 1) {
                                                    if (p.getInventory().getHelmet() != null) {
                                                        int heldur = p.getEquipment().getHelmet().getDurability();
                                                        p.getEquipment().getHelmet().setDurability((short) (heldur + 3));
                                                    }
                                                    if (p.getInventory().getChestplate() != null) {
                                                        int chestdur = p.getEquipment().getChestplate().getDurability();
                                                        p.getEquipment().getChestplate().setDurability((short) (chestdur + 3));
                                                    }
                                                    if (p.getInventory().getLeggings() != null) {
                                                        int legdur = p.getEquipment().getLeggings().getDurability();
                                                        p.getEquipment().getLeggings().setDurability((short) (legdur + 3));
                                                    }
                                                    if (p.getInventory().getBoots() != null) {
                                                        int bootdur = p.getEquipment().getBoots().getDurability();
                                                        p.getEquipment().getBoots().setDurability((short) (bootdur + 3));
                                                    }

                                                }
                                            } else if (p.getHealth() <= basedamage && p.getHealth() != 0) {
                                                p.setHealth(curhealth - curhealth);
                                            }
                                        }
                                        List<Entity> nearby = p.getNearbyEntities(2, 2, 2);
                                        for (Entity tmp : nearby)
                                            if (tmp instanceof Damageable)
                                                ((Damageable) tmp).damage(15);
                                        List<Entity> nearby2 = p.getNearbyEntities(3, 3, 3);
                                        for (Entity tmp2 : nearby2)
                                            if (tmp2 instanceof Damageable)
                                                ((Damageable) tmp2).damage(10);
                                        List<Entity> nearby3 = p.getNearbyEntities(5, 5, 5);
                                        for (Entity tmp3 : nearby3)
                                            if (tmp3 instanceof Damageable)
                                                ((Damageable) tmp3).damage(5);
                                        e.setCancelled(true);
                                        cancel();



                                }

                                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5, 2, false, false, false));
                                    if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= 2800)) {
                                        p.sendActionBar(ChatColor.RED + "--");
                                    } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= 2100)) {
                                        p.sendActionBar(ChatColor.YELLOW + "----");
                                    } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) >= 1400)) {
                                        p.sendActionBar(ChatColor.GREEN + "------");
                                    } else if (!cooldown.containsKey(p.getUniqueId()) || ((System.currentTimeMillis() - cooldown.get(p.getUniqueId())) <= 700)) {
                                        p.sendActionBar(ChatColor.BLUE + "--------");
                                    }



                        }
                }

            }.runTaskTimer(GenesisMC.getPlugin(), 0L, 5L);
        } else {
            //do nothing
        }
    }
    @EventHandler
    public void onLook(EntityTargetEvent e) {
        //EntityType en = e.getEntityType(); not needed
        if (e.getEntity() instanceof Creeper && (e.getTarget() instanceof Player)) {

            Player p = (Player) e.getTarget();
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (originid == 2356555) {
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onCreepDeath(PlayerDeathEvent e) {
        Player p = (Player) e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 2356555) {
            Random random = new Random();
                if(e.getEntity().getType() == EntityType.CREEPER){
                    Creeper killer = (Creeper) e.getEntity();
                    if(killer.isPowered()){
                        e.getDrops().add(new ItemStack(Material.CREEPER_HEAD));
                    }
                }else if(e.getEntity().getType() == EntityType.PLAYER){
                    Player killerp = e.getEntity();
                    PersistentDataContainer datak = killerp.getPersistentDataContainer();
                    int originidk = datak.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
                    if (originid == 2356555) {
                        if (p.getWorld().isThundering()) {
                            e.getDrops().add(new ItemStack(Material.CREEPER_HEAD));
                        }
                    }

                }
            }
    }

    @EventHandler
    public void onCreepDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            PersistentDataContainer data = p.getPersistentDataContainer();
            int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
            if (originid == 2356555) {
                if(e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION){
                    e.setDamage(e.getFinalDamage() - 7);
                }else{
                    if(e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK){
                        e.setDamage(e.getFinalDamage() + 2);
                    }else{e.setDamage(e.getFinalDamage() + 4);}
                }
            }
        }
    }

    @EventHandler
    public void onUseBow(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        int originid = data.get(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER);
        if (originid == 2356555) {
            if (e.getItem() != null) {
                if (e.getItem().getType().equals(Material.BOW)) {
                    e.setCancelled(true);
                }
            }
        }
    }
    }
