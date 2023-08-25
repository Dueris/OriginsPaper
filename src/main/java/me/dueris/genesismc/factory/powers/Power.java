package me.dueris.genesismc.factory.powers;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public interface Power {

    void run();
    String getPowerFile();
    ArrayList<Player> getPowerArray();
    void setActive(String tag, Boolean bool);

    HashMap<String, Boolean> powers_active = new HashMap<>();

    ArrayList<Player> fall_immunity = new ArrayList<>();
    ArrayList<Player> aerial_combatant = new ArrayList<>();
    ArrayList<Player> aqua_affinity = new ArrayList<>();
    ArrayList<Player> aquatic = new ArrayList<>();
    ArrayList<Player> arthropod = new ArrayList<>();
    ArrayList<Player> more_kinetic_damage = new ArrayList<>();
    ArrayList<Player> burning_wrath = new ArrayList<>();
    ArrayList<Player> carnivore = new ArrayList<>();
    ArrayList<Player> scare_creepers = new ArrayList<>();
    ArrayList<Player> claustrophobia = new ArrayList<>();
    ArrayList<Player> climbing = new ArrayList<>();
    ArrayList<Player> hunger_over_time = new ArrayList<>();
    ArrayList<Player> slow_falling = new ArrayList<>();
    ArrayList<Player> swim_speed = new ArrayList<>();
    ArrayList<Player> fire_immunity = new ArrayList<>();
    ArrayList<Player> fragile = new ArrayList<>();
    ArrayList<Player> fresh_air = new ArrayList<>();
    ArrayList<Player> launch_into_air = new ArrayList<>();
    ArrayList<Player> water_breathing = new ArrayList<>();
    ArrayList<Player> shulker_inventory = new ArrayList<>();
    ArrayList<Player> hotblooded = new ArrayList<>();
    ArrayList<Player> water_vulnerability = new ArrayList<>();
    ArrayList<Player> invisibility = new ArrayList<>();
    ArrayList<Player> more_exhaustion = new ArrayList<>();
    ArrayList<Player> like_air = new ArrayList<>();
    ArrayList<Player> like_water = new ArrayList<>();
    ArrayList<Player> master_of_webs = new ArrayList<>();
    ArrayList<Player> light_armor = new ArrayList<>();
    ArrayList<Player> nether_spawn = new ArrayList<>();
    ArrayList<Player> nine_lives = new ArrayList<>();
    ArrayList<Player> cat_vision = new ArrayList<>();
    ArrayList<Player> lay_eggs = new ArrayList<>();
    ArrayList<Player> phasing = new ArrayList<>();
    ArrayList<Player> burn_in_daylight = new ArrayList<>();
    ArrayList<Player> arcane_skin = new ArrayList<>();
    ArrayList<Player> end_spawn = new ArrayList<>();
    ArrayList<Player> phantomize_overlay = new ArrayList<>();
    ArrayList<Player> pumpkin_hate = new ArrayList<>();
    ArrayList<Player> extra_reach = new ArrayList<>();
    ArrayList<Player> extra_reach_attack = new ArrayList<>();
    ArrayList<Player> sprint_jump = new ArrayList<>();
    ArrayList<Player> strong_arms = new ArrayList<>();
    ArrayList<Player> natural_armor = new ArrayList<>();
    ArrayList<Player> tailwind = new ArrayList<>();
    ArrayList<Player> throw_ender_pearl = new ArrayList<>();
    ArrayList<Player> translucent = new ArrayList<>();
    ArrayList<Player> no_shield = new ArrayList<>();
    ArrayList<Player> vegetarian = new ArrayList<>();
    ArrayList<Player> velvet_paws = new ArrayList<>();
    ArrayList<Player> weak_arms = new ArrayList<>();
    ArrayList<Player> webbing = new ArrayList<>();
    ArrayList<Player> water_vision = new ArrayList<>();
    ArrayList<Player> elytra = new ArrayList<>();
    ArrayList<Player> air_from_potions = new ArrayList<>();
    ArrayList<Player> conduit_power_on_land = new ArrayList<>();
    ArrayList<Player> damage_from_potions = new ArrayList<>();
    ArrayList<Player> damage_from_snowballs = new ArrayList<>();
    ArrayList<Player> ender_particles = new ArrayList<>();
    ArrayList<Player> flame_particles = new ArrayList<>();
    ArrayList<Player> no_cobweb_slowdown = new ArrayList<>();
    ArrayList<Player> phantomize = new ArrayList<>();
    ArrayList<Player> strong_arms_break_speed = new ArrayList<>();
    ArrayList<Player> apply_effect = new ArrayList<>();
    ArrayList<Player> effect_immunity = new ArrayList<>();
    ArrayList<Player> attribute = new ArrayList<>();
    ArrayList<Player> conditioned_attribute = new ArrayList<>();
    ArrayList<Player> creative_flight = new ArrayList<>();
    ArrayList<Player> burn = new ArrayList<>();
    ArrayList<Player> restrict_armor = new ArrayList<>();
    ArrayList<Player> dmg_invulnerable = new ArrayList<>();
    ArrayList<Player> disable_regen = new ArrayList<>();
    ArrayList<Player> entity_glow = new ArrayList<>();
    ArrayList<Player> entity_group = new ArrayList<>();
    ArrayList<Player> fire_projectile = new ArrayList<>();
    ArrayList<Player> freeze = new ArrayList<>();
    ArrayList<Player> grounded = new ArrayList<>();
    ArrayList<Player> keep_inventory = new ArrayList<>();
    ArrayList<Player> model_color = new ArrayList<>();
    ArrayList<Player> night_vision = new ArrayList<>();
    ArrayList<Player> overlay = new ArrayList<>();
    ArrayList<Player> particle = new ArrayList<>();
    ArrayList<Player> recipe = new ArrayList<>();
    ArrayList<Player> self_glow = new ArrayList<>();
    ArrayList<Player> simple = new ArrayList<>();
    ArrayList<Player> stacking_status_effect = new ArrayList<>();
    ArrayList<Player> starting_equip = new ArrayList<>();
    ArrayList<Player> swimming = new ArrayList<>();
    ArrayList<Player> toggle_night_vision = new ArrayList<>();
    ArrayList<Player> toggle_power = new ArrayList<>();
    ArrayList<Player> tooltip = new ArrayList<>();
    ArrayList<Player> walk_on_fluid = new ArrayList<>();
    ArrayList<Player> bioluminescent = new ArrayList<>();
    ArrayList<Player> damage_over_time = new ArrayList<>();

    //actions
    ArrayList<Player> action_on_being_used = new ArrayList<>();
    ArrayList<Player> action_on_being_hit = new ArrayList<>();
    ArrayList<Player> action_on_block_break = new ArrayList<>();
    ArrayList<Player> action_on_block_use = new ArrayList<>();
    ArrayList<Player> action_on_callback = new ArrayList<>();
    ArrayList<Player> action_on_entity_use = new ArrayList<>();
    ArrayList<Player> action_on_hit = new ArrayList<>();
    ArrayList<Player> action_on_item_use = new ArrayList<>();
    ArrayList<Player> action_on_land = new ArrayList<>();
    ArrayList<Player> action_on_wake_up = new ArrayList<>();
    ArrayList<Player> action_ove_time = new ArrayList<>();
    ArrayList<Player> action_when_damage_taken = new ArrayList<>();
    ArrayList<Player> action_when_hit = new ArrayList<>();
    ArrayList<Player> active_self = new ArrayList<>();
    ArrayList<Player> attacker_action_when_hit = new ArrayList<>();
    ArrayList<Player> self_action_on_hit = new ArrayList<>();
    ArrayList<Player> self_action_on_kill = new ArrayList<>();
    ArrayList<Player> self_action_when_hit = new ArrayList<>();
    ArrayList<Player> target_action_on_hit = new ArrayList<>();

    //TODO: yeah gotta come back to Attribute Modifier, and Item on Item(*crys*)

    //genesis
    ArrayList<Player> hot_hands = new ArrayList<>();
    ArrayList<Player> extra_fire = new ArrayList<>();
    ArrayList<Player> bow_nope = new ArrayList<>();
    ArrayList<Player> silk_touch = new ArrayList<>();
    ArrayList<Player> explode_tick = new ArrayList<>();
    ArrayList<Player> projectile_immune = new ArrayList<>();
    ArrayList<Player> charged = new ArrayList<>();
    ArrayList<Player> felinephobia = new ArrayList<>();
    ArrayList<Player> fire_weak = new ArrayList<>();
    ArrayList<Player> gold_armour_buff = new ArrayList<>();
    ArrayList<Player> gold_item_buff = new ArrayList<>();
    ArrayList<Player> big_leap_tick = new ArrayList<>();
    ArrayList<Player> carrot_only = new ArrayList<>();
    ArrayList<Player> jump_increased = new ArrayList<>();
    ArrayList<Player> rabbit_drop_foot = new ArrayList<>();
    ArrayList<Player> decreased_explosion = new ArrayList<>();
    ArrayList<Player> creeper_head_death_drop = new ArrayList<>();
    ArrayList<Player> resist_fall = new ArrayList<>();
    ArrayList<Player> weak_biome_cold = new ArrayList<>();
    ArrayList<Player> overworld_piglin_zombified = new ArrayList<>();
    ArrayList<Player> attribute_modify_transfer = new ArrayList<>();
    ArrayList<Player> no_gravity = new ArrayList<>();

}