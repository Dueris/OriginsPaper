package me.dueris.genesismc.core.factory.powers;

import me.dueris.genesismc.core.factory.CraftApoli;
import me.dueris.genesismc.core.utils.PlayerPower;
import me.dueris.genesismc.core.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;

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

    //genesis
    public static ArrayList<String> hot_hands = new ArrayList<>();
    public static ArrayList<String> extra_fire = new ArrayList<>();
    public static ArrayList<String> entity_ignore = new ArrayList<>();
    public static ArrayList<String> bow_nope = new ArrayList<>();
    public static ArrayList<String> silk_touch = new ArrayList<>();
    public static ArrayList<String> explode_tick = new ArrayList<>();
    public static ArrayList<String> projectile_immune = new ArrayList<>();
    public static ArrayList<String> charged = new ArrayList<>();
    public static ArrayList<String> felinephobia = new ArrayList<>();
    public static ArrayList<String> fire_weak = new ArrayList<>();
    public static ArrayList<String> gold_armour_buff = new ArrayList<>();
    public static ArrayList<String> gold_item_buff = new ArrayList<>();
    public static ArrayList<String> big_leap_tick = new ArrayList<>();
    public static ArrayList<String> carrot_only = new ArrayList<>();
    public static ArrayList<String> jump_increased = new ArrayList<>();
    public static ArrayList<String> rabbit_drop_foot = new ArrayList<>();
    public static ArrayList<String> decreased_explosion = new ArrayList<>();
    public static ArrayList<String> creeper_head_death_drop = new ArrayList<>();
    public static ArrayList<String> resist_fall = new ArrayList<>();
    public static ArrayList<String> weak_biome_cold = new ArrayList<>();


    //custom origins

    public static HashMap<String, String> targetActionOnHit = new HashMap<>();

    public static void loadPowers() {



//        for (String originTag : CraftApoli.getTags()) {
//            for (String powerTag : CraftApoli.getOriginPowers(originTag)) {
//                if (powerTag.equals("origins:fall_immunity")) fall_immunity.add(originTag);
//                else if (powerTag.equals("origins:aerial_combatant")) aerial_combatant.add(originTag);
//                else if (powerTag.equals("origins:aqua_affinity")) aqua_affinity.add(originTag);
//                else if (powerTag.equals("origins:aquatic")) aquatic.add(originTag);
//                else if (powerTag.equals("origins:arthropod")) arthropod.add(originTag);
//                else if (powerTag.equals("origins:more_kinetic_damage")) more_kinetic_damage.add(originTag);
//                else if (powerTag.equals("origins:burning_wrath")) burning_wrath.add(originTag);
//                else if (powerTag.equals("origins:carnivore")) carnivore.add(originTag);
//                else if (powerTag.equals("origins:scare_creepers")) scare_creepers.add(originTag);
//                else if (powerTag.equals("origins:claustrophobia")) claustrophobia.add(originTag);
//                else if (powerTag.equals("origins:climbing")) climbing.add(originTag);
//                else if (powerTag.equals("origins:hunger_over_time")) hunger_over_time.add(originTag);
//                else if (powerTag.equals("origins:slow_falling")) slow_falling.add(originTag);
//                else if (powerTag.equals("origins:swim_speed")) swim_speed.add(originTag);
//                else if (powerTag.equals("origins:fire_immunity")) fire_immunity.add(originTag);
//                else if (powerTag.equals("origins:fragile")) fragile.add(originTag);
//                else if (powerTag.equals("origins:fresh_air")) fresh_air.add(originTag);
//                else if (powerTag.equals("origins:launch_into_air")) launch_into_air.add(originTag);
//                else if (powerTag.equals("origins:water_breathing")) water_breathing.add(originTag);
//                else if (powerTag.equals("origins:shulker_inventory")) shulker_inventory.add(originTag);
//                else if (powerTag.equals("origins:hotblooded")) hotblooded.add(originTag);
//                else if (powerTag.equals("origins:water_vulnerability")) water_vulnerability.add(originTag);
//                else if (powerTag.equals("origins:invisibility")) invisibility.add(originTag);
//                else if (powerTag.equals("origins:more_exhaustion")) more_exhaustion.add(originTag);
//                else if (powerTag.equals("origins:like_air")) like_air.add(originTag);
//                else if (powerTag.equals("origins:like_water")) like_water.add(originTag);
//                else if (powerTag.equals("origins:master_of_webs")) master_of_webs.add(originTag);
//                else if (powerTag.equals("origins:light_armor")) light_armor.add(originTag);
//                else if (powerTag.equals("origins:nether_spawn")) nether_spawn.add(originTag);
//                else if (powerTag.equals("origins:nine_lives")) nine_lives.add(originTag);
//                else if (powerTag.equals("origins:cat_vision")) cat_vision.add(originTag);
//                else if (powerTag.equals("origins:lay_eggs")) lay_eggs.add(originTag);
//                else if (powerTag.equals("origins:phasing")) phasing.add(originTag);
//                else if (powerTag.equals("origins:burn_in_daylight")) burn_in_daylight.add(originTag);
//                else if (powerTag.equals("origins:arcane_skin")) arcane_skin.add(originTag);
//                else if (powerTag.equals("origins:end_spawn")) end_spawn.add(originTag);
//                else if (powerTag.equals("origins:phantomize_overlay")) phantomize_overlay.add(originTag);
//                else if (powerTag.equals("origins:pumpkin_hate")) pumpkin_hate.add(originTag);
//                else if (powerTag.equals("origins:extra_reach")) extra_reach.add(originTag);
//                else if (powerTag.equals("origins:sprint_jump")) sprint_jump.add(originTag);
//                else if (powerTag.equals("origins:strong_arms")) strong_arms.add(originTag);
//                else if (powerTag.equals("origins:natural_armor")) natural_armor.add(originTag);
//                else if (powerTag.equals("origins:tailwind")) tailwind.add(originTag);
//                else if (powerTag.equals("origins:throw_ender_pearl")) throw_ender_pearl.add(originTag);
//                else if (powerTag.equals("origins:translucent")) translucent.add(originTag);
//                else if (powerTag.equals("origins:no_shield")) no_shield.add(originTag);
//                else if (powerTag.equals("origins:vegetarian")) vegetarian.add(originTag);
//                else if (powerTag.equals("origins:velvet_paws")) velvet_paws.add(originTag);
//                else if (powerTag.equals("origins:weak_arms")) weak_arms.add(originTag);
//                else if (powerTag.equals("origins:webbing")) webbing.add(originTag);
//                else if (powerTag.equals("origins:water_vision")) water_vision.add(originTag);
//                else if (powerTag.equals("origins:elytra_flight")) elytra.add(originTag);
//                else if (powerTag.equals("origins:air_from_potions")) air_from_potions.add(originTag);
//                else if (powerTag.equals("origins:conduit_power_on_land")) conduit_power_on_land.add(originTag);
//                else if (powerTag.equals("origins:damage_from_potions")) damage_from_potions.add(originTag);
//                else if (powerTag.equals("origins:damage_from_snowballs")) damage_from_snowballs.add(originTag);
//                else if (powerTag.equals("origins:ender_particles")) ender_particles.add(originTag);
//                else if (powerTag.equals("origins:flame_particles")) flame_particles.add(originTag);
//                else if (powerTag.equals("origins:no_cobweb_slowdown")) no_cobweb_slowdown.add(originTag);
//                else if (powerTag.equals("origins:phantomize")) phantomize.add(originTag);
//                else if (powerTag.equals("origins:strong_arms_break_speed")) strong_arms_break_speed.add(originTag);
//
//                else if (powerTag.equals("genesis:hot_hands")) hot_hands.add(originTag);
//                else if (powerTag.equals("genesis:extra_fire_tick")) extra_fire.add(originTag);
//                else if (powerTag.equals("genesis:bow_inability")) bow_nope.add(originTag);
//                else if (powerTag.equals("genesis:silk_touch")) silk_touch.add(originTag);
//                else if (powerTag.equals("genesis:explode_tick")) explode_tick.add(originTag);
//                else if (powerTag.equals("genesis:projectile-immune")) projectile_immune.add(originTag);
//                else if (powerTag.equals("genesis:charged")) charged.add(originTag);
//                else if (powerTag.equals("genesis:felinephobia")) felinephobia.add(originTag);
//                else if (powerTag.equals("genesis:fire_weak")) fire_weak.add(originTag);
//                else if (powerTag.equals("genesis:gold_armour_buff")) gold_armour_buff.add(originTag);
//                else if (powerTag.equals("genesis:gold_item_buff")) gold_item_buff.add(originTag);
//                else if (powerTag.equals("genesis:big_leap_charge")) big_leap_tick.add(originTag);
//                else if (powerTag.equals("genesis:carrots_only")) carrot_only.add(originTag);
//                else if (powerTag.equals("genesis:jump_boost")) jump_increased.add(originTag);
//                else if (powerTag.equals("genesis:drop_rabbit_foot_damage")) rabbit_drop_foot.add(originTag);
//                else if (powerTag.equals("genesis:decreased_explosion_damage")) decreased_explosion.add(originTag);
//                else if (powerTag.equals("genesis:creeper_head_death_drop")) creeper_head_death_drop.add(originTag);
//                else if (powerTag.equals("genesis:resist_fall")) resist_fall.add(originTag);
//                else if (powerTag.equals("genesis:cold_biomes_weak")) weak_biome_cold.add(originTag);
//
//                    //drop_head
//                    //entity_ignore
//
//                else {
//                    String powerType = CraftApoli.getPowerType(originTag, powerTag);
//                    if (powerType == null) return;
//                    if (powerType.equals("origins:target_action_on_hit")) targetActionOnHit.put(originTag, powerTag);
//
//                }
//            }
//        }
    }


    public static ArrayList<PlayerPower> powerArray = new ArrayList<>();

}
/*
origins:night_vision
origins:multiple
origins:attribute
origins:invulnerability
origins:prevent_item_use
origins:action_on_callback
origins:modify_exhaustion
origins:attribute
origins:restrict_armor
origins:climbing
origins:webbing
origins:carnivore
origins:fragile
origins:slow_falling            done
origins:fresh_air
origins:tailwind
origins:action_on_wake_up
origins:vegetarian
origins:fire_immunity
origins:modify_player_spawn
origins:burning_wrath
origins:effect_immunity
origins:water_vulnerability
origins:flame_particles
origins:damage_from_snowballs
origins:damage_from_potions
origins:elytra_flight
origins:launch_into_air
origins:aerial_combatant
origins:light_armor
origins:claustrophobia
origins:more_kinetic_damage
origins:water_vulnerability
origins:pumpkin_hate
origins:extra_reach
origins:ender_particles
origins:damage_from_potions
genesis:silk_touch
origins:fall_immunity
origins:sprint_jump
origins:velvet_paws
origins:nine_lives
genesis:felinephobia
origins:cat_vision
origins:water_breathing
origins:water_vision
origins:aqua_affinity
origins:swim_speed
origins:aquatic
origins:conduit_power_on_land
origins:air_from_potions
origins:phantomize
origins:translucent
origins:burn_in_daylight
origins:fragile
origins:phantomize_overlay
origins:shulker_inventory
origins:natural_armor
origins:strong_arms
origins:strong_arms_break_speed
origins:no_shield
origins:more_exhaustion
genesis:flamable
genesis:cookie_buff
genesis:glowing_entity
genesis:jukebox_anchor
genesis:little_fairy
genesis:nausea_action_eat_meat
genesis:sparkle_particles
genesis:treasure_loot_bonus
genesis:carrots_only
genesis:jump_boost
genesis:big_leap_charge
genesis:drop_rabbit_foot_damage
genesis:resist_fall
origins:fragile
genesis:creative_flight
genesis:creative_flight_rain_cancel
genesis:flower_boost_health
origins:slow_falling
origins:fragile
genesis:night_weaker
genesis:armor_decay
genesis:block_stack_buff
genesis:deep_dark_spawn
genesis:echo_pulse
genesis:daylight_debuff
genesis:sculk_growth_death
genesis:sonic_boom
genesis:warden_ignore
genesis:charged
genesis:creeper_ignore
genesis:explosion_tick
genesis:felinephobia
genesis:burn_hot_biomes
genesis:slime_block_bounce
genesis:split_slime
genesis:big_leap_charge
genesis:jump_boost
genesis:gold_armour_buff
genesis:gold_item_buff
genesis:piglin_ignore_brute_exception
origins:modify_player_spawn
genesis:overworld_piglin_zombified
genesis:blue_fire_weak
origins:modify_damage_dealt
origins:modify_damage_taken
origins:damage_over_time
origins:attribute
origins:modify_player_spawn
origins:modify_exhaustion
origins:restrict_armor
origins:model_color
origins:multiple
origins:active_self
origins:attribute
origins:modify_exhaustion
 */