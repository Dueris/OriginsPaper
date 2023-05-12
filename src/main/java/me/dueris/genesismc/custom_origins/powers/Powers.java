package me.dueris.genesismc.custom_origins.powers;

import me.dueris.api.factory.CustomOriginAPI;
import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Random;

import static org.bukkit.Material.*;

public class Powers implements Listener {

    public static ArrayList<String> fall_immunity = new ArrayList<>();
    public static ArrayList<String> aerial_combatant = new ArrayList<>();
    public static ArrayList<String> aqua_affinity = new ArrayList<>();
    public static ArrayList<String> aquatic = new ArrayList<>();
    public static ArrayList<String> arthropod = new ArrayList<>();
    public static ArrayList<String> more_kinetic_damage = new ArrayList<>();
    public static ArrayList<String> burning_wrath = new ArrayList<>();
    public static ArrayList<String> carnivore = new ArrayList<>();
    public static ArrayList<String> scare_creepers = new ArrayList<>();
    public static ArrayList<String> claustrophobia = new ArrayList<>();
    public static ArrayList<String> climbing = new ArrayList<>();
    public static ArrayList<String> hunger_over_time = new ArrayList<>();
    public static ArrayList<String> slow_falling = new ArrayList<>();
    public static ArrayList<String> swim_speed = new ArrayList<>();
    public static ArrayList<String> fire_immunity = new ArrayList<>();
    public static ArrayList<String> fragile = new ArrayList<>();
    public static ArrayList<String> fresh_air = new ArrayList<>();
    public static ArrayList<String> launch_into_air = new ArrayList<>();
    public static ArrayList<String> water_breathing = new ArrayList<>();
    public static ArrayList<String> shulker_inventory = new ArrayList<>();
    public static ArrayList<String> hotblooded = new ArrayList<>();
    public static ArrayList<String> water_vulnerability = new ArrayList<>();
    public static ArrayList<String> invisibility = new ArrayList<>();
    public static ArrayList<String> more_exhaustion = new ArrayList<>();
    public static ArrayList<String> like_air = new ArrayList<>();
    public static ArrayList<String> like_water = new ArrayList<>();
    public static ArrayList<String> master_of_webs = new ArrayList<>();
    public static ArrayList<String> light_armor = new ArrayList<>();
    public static ArrayList<String> nether_spawn = new ArrayList<>();
    public static ArrayList<String> nine_lives = new ArrayList<>();
    public static ArrayList<String> cat_vision = new ArrayList<>();
    public static ArrayList<String> lay_eggs = new ArrayList<>();
    public static ArrayList<String> phasing = new ArrayList<>();
    public static ArrayList<String> burn_in_daylight = new ArrayList<>();
    public static ArrayList<String> arcane_skin = new ArrayList<>();
    public static ArrayList<String> end_spawn = new ArrayList<>();
    public static ArrayList<String> phantomize_overlay = new ArrayList<>();
    public static ArrayList<String> pumpkin_hate = new ArrayList<>();
    public static ArrayList<String> extra_reach = new ArrayList<>();
    public static ArrayList<String> sprint_jump = new ArrayList<>();
    public static ArrayList<String> strong_arms = new ArrayList<>();
    public static ArrayList<String> natural_armor = new ArrayList<>();
    public static ArrayList<String> tailwind = new ArrayList<>();
    public static ArrayList<String> throw_ender_pearl = new ArrayList<>();
    public static ArrayList<String> translucent = new ArrayList<>();
    public static ArrayList<String> no_shield = new ArrayList<>();
    public static ArrayList<String> vegetarian = new ArrayList<>();
    public static ArrayList<String> velvet_paws = new ArrayList<>();
    public static ArrayList<String> weak_arms = new ArrayList<>();
    public static ArrayList<String> webbing = new ArrayList<>();
    public static ArrayList<String> water_vision = new ArrayList<>();
    public static ArrayList<String> elytra = new ArrayList<>();
    public static ArrayList<String> air_from_potions = new ArrayList<>();
    public static ArrayList<String> conduit_power_on_land = new ArrayList<>();
    public static ArrayList<String> damage_from_potions = new ArrayList<>();
    public static ArrayList<String> damage_from_snowballs = new ArrayList<>();
    public static ArrayList<String> ender_particles = new ArrayList<>();
    public static ArrayList<String> flame_particles = new ArrayList<>();
    public static ArrayList<String> no_cobweb_slowdown = new ArrayList<>();
    public static ArrayList<String> phantomize = new ArrayList<>();
    public static ArrayList<String> strong_arms_break_speed = new ArrayList<>();

    public static void loadPowers() {
        //blazeborn
        nether_spawn.add("genesis:origin-blazeborn");
        burning_wrath.add("genesis:origin-blazeborn");
        fire_immunity.add("genesis:origin-blazeborn");
        water_vulnerability.add("genesis:origin-blazeborn");
        hotblooded.add("genesis:origin-blazeborn");
        //add You are much weaker in colder biomes and at high altitudes
        //add set player on fire on hit

        for (String originTag : CustomOriginAPI.getCustomOriginTags()) {
            for (String power : CustomOriginAPI.getCustomOriginPowers(originTag)) {
                if (power.equals("origins:fall_immunity")) fall_immunity.add(originTag);
                else if (power.equals("origins:aerial_combatant")) aerial_combatant.add(originTag);
                else if (power.equals("origins:aqua_affinity")) aqua_affinity.add(originTag);
                else if (power.equals("origins:aquatic")) aquatic.add(originTag);
                else if (power.equals("origins:arthropod")) arthropod.add(originTag);
                else if (power.equals("origins:more_kinetic_damage")) more_kinetic_damage.add(originTag);
                else if (power.equals("origins:burning_wrath")) burning_wrath.add(originTag);
                else if (power.equals("origins:carnivore")) carnivore.add(originTag);
                else if (power.equals("origins:scare_creepers")) scare_creepers.add(originTag);
                else if (power.equals("origins:claustrophobia")) claustrophobia.add(originTag);
                else if (power.equals("origins:climbing")) climbing.add(originTag);
                else if (power.equals("origins:hunger_over_time")) hunger_over_time.add(originTag);
                else if (power.equals("origins:slow_falling")) slow_falling.add(originTag);
                else if (power.equals("origins:swim_speed")) swim_speed.add(originTag);
                else if (power.equals("origins:fire_immunity")) fire_immunity.add(originTag);
                else if (power.equals("origins:fragile")) fragile.add(originTag);
                else if (power.equals("origins:fresh_air")) fresh_air.add(originTag);
                else if (power.equals("origins:launch_into_air")) launch_into_air.add(originTag);
                else if (power.equals("origins:water_breathing")) water_breathing.add(originTag);
                else if (power.equals("origins:shulker_inventory")) shulker_inventory.add(originTag);
                else if (power.equals("origins:hotblooded")) hotblooded.add(originTag);
                else if (power.equals("origins:water_vulnerability")) water_vulnerability.add(originTag);
                else if (power.equals("origins:invisibility")) invisibility.add(originTag);
                else if (power.equals("origins:more_exhaustion")) more_exhaustion.add(originTag);
                else if (power.equals("origins:like_air")) like_air.add(originTag);
                else if (power.equals("origins:like_water")) like_water.add(originTag);
                else if (power.equals("origins:master_of_webs")) master_of_webs.add(originTag);
                else if (power.equals("origins:light_armor")) light_armor.add(originTag);
                else if (power.equals("origins:nether_spawn")) nether_spawn.add(originTag);
                else if (power.equals("origins:nine_lives")) nine_lives.add(originTag);
                else if (power.equals("origins:cat_vision")) cat_vision.add(originTag);
                else if (power.equals("origins:lay_eggs")) lay_eggs.add(originTag);
                else if (power.equals("origins:phasing")) phasing.add(originTag);
                else if (power.equals("origins:burn_in_daylight")) burn_in_daylight.add(originTag);
                else if (power.equals("origins:arcane_skin")) arcane_skin.add(originTag);
                else if (power.equals("origins:end_spawn")) end_spawn.add(originTag);
                else if (power.equals("origins:phantomize_overlay")) phantomize_overlay.add(originTag);
                else if (power.equals("origins:pumpkin_hate")) pumpkin_hate.add(originTag);
                else if (power.equals("origins:extra_reach")) extra_reach.add(originTag);
                else if (power.equals("origins:sprint_jump")) sprint_jump.add(originTag);
                else if (power.equals("origins:strong_arms")) strong_arms.add(originTag);
                else if (power.equals("origins:natural_armor")) natural_armor.add(originTag);
                else if (power.equals("origins:tailwind")) tailwind.add(originTag);
                else if (power.equals("origins:throw_ender_pearl")) throw_ender_pearl.add(originTag);
                else if (power.equals("origins:translucent")) translucent.add(originTag);
                else if (power.equals("origins:no_shield")) no_shield.add(originTag);
                else if (power.equals("origins:vegetarian")) vegetarian.add(originTag);
                else if (power.equals("origins:velvet_paws")) velvet_paws.add(originTag);
                else if (power.equals("origins:weak_arms")) weak_arms.add(originTag);
                else if (power.equals("origins:webbing")) webbing.add(originTag);
                else if (power.equals("origins:water_vision")) water_vision.add(originTag);
                else if (power.equals("origins:elytra")) elytra.add(originTag);
                else if (power.equals("origins:air_from_potions")) air_from_potions.add(originTag);
                else if (power.equals("origins:conduit_power_on_land")) conduit_power_on_land.add(originTag);
                else if (power.equals("origins:damage_from_potions")) damage_from_potions.add(originTag);
                else if (power.equals("origins:damage_from_snowballs")) damage_from_snowballs.add(originTag);
                else if (power.equals("origins:ender_particles")) ender_particles.add(originTag);
                else if (power.equals("origins:flame_particles")) flame_particles.add(originTag);
                else if (power.equals("origins:no_cobweb_slowdown")) no_cobweb_slowdown.add(originTag);
                else if (power.equals("origins:phantomize")) phantomize.add(originTag);
                else if (power.equals("origins:strong_arms_break_speed")) strong_arms_break_speed.add(originTag);
            }
        }
    }

    //fall_immunity
    @EventHandler
    public void acrobatics(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (fall_immunity.contains(origintag)) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(true);
            }
        }
    }

    //fire_immunity
    @EventHandler
    public void fireImmunity(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        @Nullable String origintag =  p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (!fire_immunity.contains(origintag)) return;
        if (!(e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)) return;
        e.setCancelled(true);
    }

    //hotblooded
    @EventHandler
    public void hotblooded(EntityPotionEffectEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        @Nullable String origintag =  p.getPersistentDataContainer().get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (!hotblooded.contains(origintag)) return;
        if (e.getOldEffect() == null) return;
        if (e.getOldEffect().getType().getId() == PotionEffectType.HUNGER.getId() || e.getOldEffect().getType().getId() == PotionEffectType.HUNGER.getId()) return;
        if (e.getModifiedType().getId() == PotionEffectType.HUNGER.getId()) e.setCancelled(true);
        if (e.getModifiedType().getId() == PotionEffectType.POISON.getId()) e.setCancelled(true);
    }
    //nether_spawn
    @EventHandler
    public void netherSpawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (nether_spawn.contains(origintag)) {
            if (!(e.isBedSpawn() || e.isAnchorSpawn())) {
                Location spawnLocation = null;
                for (World world : Bukkit.getWorlds()) {
                    if (world.getEnvironment() == World.Environment.NETHER) {

                        Random random = new Random();
                        Location location = new Location(world, random.nextInt(-300, 300), 32, random.nextInt(-300, 300));

                        for (int x = (int) (location.getX()-100); x < location.getX()+100; x++) {
                            for (int z = (int) (location.getZ()-100); z < location.getZ()+100; z++) {
                                yLoop:
                                for (int y = (int) (location.getY()); y < location.getY()+68; y++) {
                                    if (new Location(world, x, y, z).getBlock().getType() != AIR) continue;
                                    if (new Location(world, x, y+1, z).getBlock().getType() != AIR) continue;
                                    Material blockBeneath = new Location(world, x, y-1, z).getBlock().getType();
                                    if (blockBeneath == AIR || blockBeneath == LAVA || blockBeneath == FIRE || blockBeneath == SOUL_FIRE) continue;

                                    for (int potentialX = (int) (new Location(world, x, y, z).getX()-2); potentialX < new Location(world, x, y, z).getX()+2; potentialX++) {
                                        for (int potentialY = (int) (new Location(world, x, y, z).getY()); potentialY < new Location(world, x, y, z).getY()+2; potentialY++) {
                                            for (int potentialZ = (int) (new Location(world, x, y, z).getZ()-2); potentialZ < new Location(world, x, y, z).getZ()+2; potentialZ++) {
                                                if (new Location(world, potentialX, potentialY, potentialZ).getBlock().getType() != AIR) continue yLoop;
                                            }
                                        }
                                    }
                                    spawnLocation = (new Location(world, x+0.5, y, z+0.5));
                                }
                            }
                        }
                        break;
                    }
                }
                if (spawnLocation == null) return;
                e.setRespawnLocation(spawnLocation);
            }

        }
    }
}
