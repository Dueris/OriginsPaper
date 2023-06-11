package me.dueris.genesismc.core.factory.powers;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class Powers implements Listener {

    public static ArrayList<Player> fall_immunity = new ArrayList<>();
    public static ArrayList<Player> aerial_combatant = new ArrayList<>();
    public static ArrayList<Player> aqua_affinity = new ArrayList<>();
    public static ArrayList<Player> aquatic = new ArrayList<>();
    public static ArrayList<Player> arthropod = new ArrayList<>();
    public static ArrayList<Player> more_kinetic_damage = new ArrayList<>();
    public static ArrayList<Player> burning_wrath = new ArrayList<>();
    public static ArrayList<Player> carnivore = new ArrayList<>();
    public static ArrayList<Player> scare_creepers = new ArrayList<>();
    public static ArrayList<Player> claustrophobia = new ArrayList<>();
    public static ArrayList<Player> climbing = new ArrayList<>();
    public static ArrayList<Player> hunger_over_time = new ArrayList<>();
    public static ArrayList<Player> slow_falling = new ArrayList<>();
    public static ArrayList<Player> swim_speed = new ArrayList<>();
    public static ArrayList<Player> fire_immunity = new ArrayList<>();
    public static ArrayList<Player> fragile = new ArrayList<>();
    public static ArrayList<Player> fresh_air = new ArrayList<>();
    public static ArrayList<Player> launch_into_air = new ArrayList<>();
    public static ArrayList<Player> water_breathing = new ArrayList<>();
    public static ArrayList<Player> shulker_inventory = new ArrayList<>();
    public static ArrayList<Player> hotblooded = new ArrayList<>();
    public static ArrayList<Player> water_vulnerability = new ArrayList<>();
    public static ArrayList<Player> invisibility = new ArrayList<>();
    public static ArrayList<Player> more_exhaustion = new ArrayList<>();
    public static ArrayList<Player> like_air = new ArrayList<>();
    public static ArrayList<Player> like_water = new ArrayList<>();
    public static ArrayList<Player> master_of_webs = new ArrayList<>();
    public static ArrayList<Player> light_armor = new ArrayList<>();
    public static ArrayList<Player> nether_spawn = new ArrayList<>();
    public static ArrayList<Player> nine_lives = new ArrayList<>();
    public static ArrayList<Player> cat_vision = new ArrayList<>();
    public static ArrayList<Player> lay_eggs = new ArrayList<>();
    public static ArrayList<Player> phasing = new ArrayList<>();
    public static ArrayList<Player> burn_in_daylight = new ArrayList<>();
    public static ArrayList<Player> arcane_skin = new ArrayList<>();
    public static ArrayList<Player> end_spawn = new ArrayList<>();
    public static ArrayList<Player> phantomize_overlay = new ArrayList<>();
    public static ArrayList<Player> pumpkin_hate = new ArrayList<>();
    public static ArrayList<Player> extra_reach = new ArrayList<>();
    public static ArrayList<Player> sprint_jump = new ArrayList<>();
    public static ArrayList<Player> strong_arms = new ArrayList<>();
    public static ArrayList<Player> natural_armor = new ArrayList<>();
    public static ArrayList<Player> tailwind = new ArrayList<>();
    public static ArrayList<Player> throw_ender_pearl = new ArrayList<>();
    public static ArrayList<Player> translucent = new ArrayList<>();
    public static ArrayList<Player> no_shield = new ArrayList<>();
    public static ArrayList<Player> vegetarian = new ArrayList<>();
    public static ArrayList<Player> velvet_paws = new ArrayList<>();
    public static ArrayList<Player> weak_arms = new ArrayList<>();
    public static ArrayList<Player> webbing = new ArrayList<>();
    public static ArrayList<Player> water_vision = new ArrayList<>();
    public static ArrayList<Player> elytra = new ArrayList<>();
    public static ArrayList<Player> air_from_potions = new ArrayList<>();
    public static ArrayList<Player> conduit_power_on_land = new ArrayList<>();
    public static ArrayList<Player> damage_from_potions = new ArrayList<>();
    public static ArrayList<Player> damage_from_snowballs = new ArrayList<>();
    public static ArrayList<Player> ender_particles = new ArrayList<>();
    public static ArrayList<Player> flame_particles = new ArrayList<>();
    public static ArrayList<Player> no_cobweb_slowdown = new ArrayList<>();
    public static ArrayList<Player> phantomize = new ArrayList<>();
    public static ArrayList<Player> strong_arms_break_speed = new ArrayList<>();
    public static ArrayList<Player> apply_effect = new ArrayList<>();

    //genesis
    public static ArrayList<Player> hot_hands = new ArrayList<>();
    public static ArrayList<Player> extra_fire = new ArrayList<>();
    public static ArrayList<Player> entity_ignore = new ArrayList<>();
    public static ArrayList<Player> bow_nope = new ArrayList<>();
    public static ArrayList<Player> silk_touch = new ArrayList<>();
    public static ArrayList<Player> explode_tick = new ArrayList<>();
    public static ArrayList<Player> projectile_immune = new ArrayList<>();
    public static ArrayList<Player> charged = new ArrayList<>();
    public static ArrayList<Player> felinephobia = new ArrayList<>();
    public static ArrayList<Player> fire_weak = new ArrayList<>();
    public static ArrayList<Player> gold_armour_buff = new ArrayList<>();
    public static ArrayList<Player> gold_item_buff = new ArrayList<>();
    public static ArrayList<Player> big_leap_tick = new ArrayList<>();
    public static ArrayList<Player> carrot_only = new ArrayList<>();
    public static ArrayList<Player> jump_increased = new ArrayList<>();
    public static ArrayList<Player> rabbit_drop_foot = new ArrayList<>();
    public static ArrayList<Player> decreased_explosion = new ArrayList<>();
    public static ArrayList<Player> creeper_head_death_drop = new ArrayList<>();
    public static ArrayList<Player> resist_fall = new ArrayList<>();
    public static ArrayList<Player> weak_biome_cold = new ArrayList<>();


    //custom origins

    public static ArrayList<Player> attribute_modify_transfer = new ArrayList<>();

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
origins:modify_Player_spawn
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
origins:modify_Player_spawn
genesis:overworld_piglin_zombified
genesis:blue_fire_weak
origins:modify_damage_dealt
origins:modify_damage_taken
origins:damage_over_time
origins:attribute
origins:modify_Player_spawn
origins:modify_exhaustion
origins:restrict_armor
origins:model_color
origins:multiple
origins:active_self
origins:attribute
origins:modify_exhaustion
 */